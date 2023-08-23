package com.example.camundaCloud.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.camundaCloud.workers.Worker;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;

@Service
public class ProcessService {
    
     @Autowired
     private ZeebeClient client;
     //this client is optional private CamundaOperateClient operateClient;


     private final static Logger LOG = LoggerFactory.getLogger(ProcessService.class);

     
     public ProcessService() throws OperateException{
        //only if needed we connect to operate if operate is not running delete this
      //  SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
       // this.operateClient= new CamundaOperateClient.Builder().operateUrl("http://localhost:8081").authentication(sa).build();
        
     }
     
     

     //Get the processState
     /**
      * 
      * @param processInstanceKey
      * @return
      * @throws NumberFormatException
      * @throws OperateException
      */
     public String getProcessStateByKey(String processInstanceKey) throws NumberFormatException, OperateException{
        try{
            if(Worker.currentProcessState.get(processInstanceKey)!=null)
        return Worker.currentProcessState.get(processInstanceKey);
        else return "NOTFOUND";
    }
        catch(Exception e){ return "NOTFOUND";}
    }
      //Get processState 
     /**
      * 
      * @param processInstanceKey
      * @return
      */
     public ResponseEntity<Object> getProcessState(String processInstanceKey){
                 Map<String,Object> res =new HashMap<String , Object>();
        try { 
            
        res.put("processState",this.getProcessStateByKey(processInstanceKey));
        return ResponseEntity.ok(res);
    } catch (Exception e) {
        // if the operate won't answer
        res.put("processState","NOTFOUND");
        return ResponseEntity.ok(res);}
     }

     //Get processState using the Operate client it also optional try getProcessState
     /**
      * 
      * @param processInstanceKey
      * @return
      */
    /* public ResponseEntity<Object> getProcessStateOperate(String processInstanceKey){
                 Map<String,Object> res =new HashMap<String , Object>();
        try { 
        res.put("processState", this.operateClient.getProcessInstance(Long.parseLong(processInstanceKey)).getState().toString());
        return ResponseEntity.ok(res);
    } catch (Exception e) {
        // if the operate won't answer
        res.put("processState","NOTFOUNDAvailable");
        return ResponseEntity.ok(res);}
     }*/

     //send startCommand to Zeebe engine 
     /**
      * 
      * @param bpmnProcessId
      * @return
      */
     public ResponseEntity<Object> startProcess(String bpmnProcessId) {
        try{ 
            Map<String,Object> res =new HashMap<String , Object>();
        final ProcessInstanceEvent event= client.newCreateInstanceCommand()
        .bpmnProcessId(bpmnProcessId)
        .latestVersion()             //latest version of the bpmn
        .send()
        .join();

        // trying to create a process instance with result 
        // this is a futur response that waites until the process is done to return result
        /* final ProcessInstanceResult processInstanceResult= client.newCreateInstanceCommand()
          .bpmnProcessId(bpmnPnstanceId)
          .latestVersion()
          .withResult()
          .send()
          .join();
           res=processInstanceResult.getVariables();*/
        
        res.put("status","started");
        res.put("processDefinitionKey",event.getProcessDefinitionKey());
        res.put("bpmnProcessId",event.getBpmnProcessId());
        res.put("version",event.getVersion());
        res.put("processInstanceKey",event.getProcessInstanceKey());
        LOG.info(res.toString());
        return ResponseEntity.ok(res);
        
       } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to start process: " + e.getMessage());
       }
     
      }

     //get the current task of processInstance
     /**
      * 
      * @param processInstanceKey
      * @return
      */ 
     public ResponseEntity<Object> getTask(String processInstanceKey)
     {  
        Map<String,Object> res =new HashMap<String , Object>();
        try{ 
            
            String state=this.getProcessStateByKey(processInstanceKey);
            res.put("processInstanceState",state);
            HashMap<String,ActivatedJob> jobs=Worker.getCurrentJobs();
            
            // Check if the job (task) is available for processing
            if (jobs != null && jobs.get(processInstanceKey)!=null) {
                // get the formkey from the job 
                ActivatedJob instanceTask=jobs.get(processInstanceKey);
                res.put("formId",instanceTask.getCustomHeaders().get("io.camunda.zeebe:formKey"));
                res.put("taskId",instanceTask.getElementId());
                 
                
            } else {
              res.put("notification","No tasks available, processInstance is "+state);
            }
                    return ResponseEntity.ok(res);

                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to get task: " + e.getMessage());
                
                }
            }  

     //complete the current task of an activated process
     /**
      * 
      * @param processInstanceKey
      * @param taskId
      * @param taskVariables
      * @return
      */
     public ResponseEntity<Object> completeTask( String processInstanceKey,String taskId,
                                             Map<String, Object> taskVariables)
     {        Map<String,Object> res =new HashMap<String , Object>();
        try{
        //getting the state first
        String state=this.getProcessStateByKey(processInstanceKey);
        if(state.equals("ACTIVE")){

       //getting taskinstancekey
       //todo remember to handle error in case of non process existance
       HashMap<String,ActivatedJob> jobs=Worker.getCurrentJobs();
       //client.newActivateJobsCommand().jobType("processInstanceKey").maxJobsToActivate(0).fetchVariables(null).
       // check if validity of variables
       
       if(!check(taskVariables)){
        
        res.put("notification","verirfy your info");
    }

        else{
       if(jobs!=null) {
        // get the formkey from the job 
            ActivatedJob instanceTask=jobs.get(processInstanceKey);
            if(instanceTask!=null){
            if(taskId.equals(instanceTask.getElementId())){    
            //completing the task
            CompleteJobResponse response = client.newCompleteCommand(instanceTask.getKey())
            .variables(taskVariables).send()
            .join();
                
        
        res.put("status","completed");
        res.put("processInstanceState",this.getProcessStateByKey(processInstanceKey));
        
        
            }
            else{
                res.put("notification","trying to complete a different task, wait until the state is updated");
            }
            }
            else {             
              res.put("notification","there is no job associated to this processinstacekey");
            }
       }   
       else {            
            res.put("notification","no jobs avaiblable");
       }

       }
       
      }
      else{res.put("notification","this processInstance is "+state);}
      
      return ResponseEntity.ok(res); 
    }
      catch (Exception e){
        res.put("notification","Failed to complete task: " + e.getMessage());
        return ResponseEntity.badRequest().body(res);
      }


   }

   //cancel a running process
   /**
    * 
    * @param processInstanceKey
    * @return
    * @throws NumberFormatException
    * @throws OperateException
    */
   public ResponseEntity<Object> cancelProcess(String processInstanceKey) throws NumberFormatException, OperateException {
        Map<String,Object> res =new HashMap<String , Object>();
       
       try{ client.newCancelInstanceCommand(Long.parseLong(processInstanceKey))
        .send()
        .join();
       //UPDATE PROCESS STATE
       Worker.putProcessState(processInstanceKey, "CANCELED");                                          
       res.put("state","canceled");
       res.put("processInstanceState",this.getProcessStateByKey(processInstanceKey)); 
       
                                        
       return  ResponseEntity.ok(res);
       }
       catch(Exception e){
         res.put("notification","Failed to cancel process: " + e.getMessage());
        return ResponseEntity.badRequest().body(res);
       }
    }

    //custom function to chech the validity of data 
    /**
     * 
     * @param taskVariables variable to check their validity if it is necessary
     * @return
     */
    public boolean check( Map<String, Object> taskVariables){

     return true;
    }
}
