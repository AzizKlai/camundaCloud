package com.example.camundaCloud.services;

import java.util.HashMap;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.camundaCloud.controllers.ProcessController;
import com.example.camundaCloud.global.Global;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.dto.ProcessInstanceState;
import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.ModifyProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.response.ActivateJobsResponse;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;
import io.camunda.zeebe.client.api.response.CancelProcessInstanceResponse;

@Service
public class ProcessService {
    
     @Autowired
     private ZeebeClient client;
     
     private CamundaOperateClient operateClient;
     public ProcessService() throws OperateException{
        SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
        this.operateClient= new CamundaOperateClient.Builder().operateUrl("http://localhost:8081").authentication(sa).build();
        
     }
     private final static Logger LOG = LoggerFactory.getLogger(ProcessService.class);
     
     
     

     //Get the processState
     public String getProcessState(String processInstanceKey) throws NumberFormatException, OperateException{
        try{
        return Global.currentProcessState.get(processInstanceKey);
    }
        catch(Exception e){ return "doesn't exist";}
    }

     //Get processState using the Operate client
     public ResponseEntity<Object> getProcessStateOperate(String processInstanceKey){
                 Map<String,Object> res =new HashMap<String , Object>();
        try { 
        res.put("processState", this.operateClient.getProcessInstance(Long.parseLong(processInstanceKey)).getState().toString());
        return ResponseEntity.ok(res);
    } catch (Exception e) {
        // if the operate won't answer
        res.put("processState","noStateAvailable");
        return ResponseEntity.ok(res);}
     }

     //send startCommand to Zeebe engine
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
     public ResponseEntity<Object> getTask(String processInstanceKey)
     {  
        Map<String,Object> res =new HashMap<String , Object>();
        try{ 
            
            res.put("processInstanceState",this.getProcessState(processInstanceKey));
            HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
            
            // Check if the job (task) is available for processing
            if (jobs != null && jobs.get(processInstanceKey)!=null) {
                // get the formkey from the job 
                ActivatedJob instanceTask=jobs.get(processInstanceKey);
                res.put("formId",instanceTask.getCustomHeaders().get("io.camunda.zeebe:formKey"));
                res.put("taskId",instanceTask.getElementId());
                 
                
            } else {
              res.put("notification","No tasks available.");
            }
                    System.out.println(res);
                    return ResponseEntity.ok(res);

                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to get task: " + e.getMessage());
                
                }
            }  


     public ResponseEntity<Object> completeTask( String processInstanceKey,String taskId,
                                             Map<String, Object> taskVariables)
     {        Map<String,Object> res =new HashMap<String , Object>();
        try{
       //getting taskinstacekey
       //todo remember to handle error in case of non process existance
       HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
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
                System.out.println("from srevice"+instanceTask.getElementId()+" "+taskId+" "+(taskId.equals(instanceTask.getElementId())));
            if(taskId.equals(instanceTask.getElementId())){    
            //completing the task
            CompleteJobResponse response = client.newCompleteCommand(instanceTask.getKey())
            .variables(taskVariables).send()
            .join();
                
        
        res.put("status","completed");
        res.put("processInstanceState",this.getProcessState(processInstanceKey));

        
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
       
       System.out.println(res);
        return ResponseEntity.ok(res); 
    }
      catch (Exception e){
        res.put("notification","Failed to complete task: " + e.getMessage());
        return ResponseEntity.badRequest().body(res);
      }


   }

   //cancel a running process
   public ResponseEntity<Object> cancelProcess(String processInstanceKey) throws NumberFormatException, OperateException {
        Map<String,Object> res =new HashMap<String , Object>();
       
       try{ client.newCancelInstanceCommand(Long.parseLong(processInstanceKey))
        .send()
        .join();
       //UPDATE PROCESS STATE
       Global.putProcessState(processInstanceKey, "CANCELED");                                          
       res.put("state","canceled");
       res.put("processInstanceState",this.getProcessState(processInstanceKey)); 
       
                                        
       return  ResponseEntity.ok(res);
       }
       catch(Exception e){
         res.put("notification","Failed to cancel process: " + e.getMessage());
        return ResponseEntity.badRequest().body(res);
       }
    }

    //custom function to chech the validity of data
    public boolean check( Map<String, Object> taskVariables){
        if(taskVariables.get("test")=="false")
        return false;
        else return true;
    }
}
