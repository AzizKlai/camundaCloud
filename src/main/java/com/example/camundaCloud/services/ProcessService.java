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
     
     
     

     //this func get the processstate
     public String getProcessState(String processInstanceKey) throws NumberFormatException, OperateException{
        try{
        return Global.currentProcessState.get(processInstanceKey);
    }
        catch(Exception e){ return "doesn't exist";}
    }
     // this one get the process State using operate client
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


     public ResponseEntity<Object> startProcess(String bpmnProcessId) {
        // TODO Auto-generated method stub
        try{ 
            Map<String,Object> res =new HashMap<String , Object>();
        final ProcessInstanceEvent event= client.newCreateInstanceCommand()
        .bpmnProcessId(bpmnProcessId)
        .latestVersion()
        .send()
        .join();
                    // trying to create a process instance with result
       /*  final ProcessInstanceResult processInstanceResult= client.newCreateInstanceCommand()
        .bpmnProcessId(bpmnPnstanceId)
        .latestVersion()
        .withResult()
        .send()
        .join();
        processInstanceResult.getVariables();*/
        //client.newCompleteCommand(event.)
        
        res.put("status","started");
        res.put("processDefinitionKey",event.getProcessDefinitionKey());
        res.put("bpmnProcessId",event.getBpmnProcessId());
        res.put("version",event.getVersion());
        res.put("processInstanceKey",event.getProcessInstanceKey());
        LOG.info(res.toString());
       // client.newCompleteCommand()
        return ResponseEntity.ok(res);
        
       } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to start process: " + e.getMessage());
       }
     
      }


     public ResponseEntity<Object> getTask(String processInstanceKey)
     {  
        //operateClient.searchProcessInstances()
        Map<String,Object> res =new HashMap<String , Object>();
        try{ 
            res.put("processInstanceState",this.getProcessState(processInstanceKey));
            HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
            // get the formkey from the job 
            ActivatedJob instanceTask=jobs.get(processInstanceKey);
            //.get(processInstanceKey).toString();
            // Check if the job (task) is available for processing
            if (jobs != null && instanceTask!=null) {
                res.put("formId",instanceTask.getCustomHeaders().get("io.camunda.zeebe:formKey"));
                res.put("task",instanceTask.getElementId());

                //  ModifyProcessInstanceCommandStep1 modify = client.newModifyProcessInstanceCommand(Long.parseLong(processInstanceKey));
                
                // modify.terminateElement(instanceTask.getElementInstanceKey()).send();
                // modify.activateElement("Activity_test1").send();
              
                
            } else {
              res.put("notifications","No tasks available.");
            }
                    System.out.println(res);
                    return ResponseEntity.ok(res);

                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to get task: " + e.getMessage());
                
                }
            }  


     public ResponseEntity<Object> completeTask( String processInstanceKey,
                                             Map<String, Object> taskVariables)
     {        Map<String,Object> res =new HashMap<String , Object>();
        try{
     //  client.newWorker().jobType(processInstanceKey).
       // getting taskinstacekey
       //todo remember to handle error in case of non process existance
       HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
       //client.newActivateJobsCommand().jobType("processInstanceKey").maxJobsToActivate(0).fetchVariables(null).
       // check if validity of variables
       if(!check(taskVariables)){
        
        res.put("notification","verirfy you info");
    }

        else{
       if(jobs!=null) {
        // get the formkey from the job 
            ActivatedJob instanceTask=jobs.get(processInstanceKey);
            if(instanceTask!=null){
        CompleteJobResponse response = client.newCompleteCommand(instanceTask.getKey())
        .variables(taskVariables).send()
                .join();
          //test modify command     
        
        
         //res.put("modifycommandtest",modify.);
        //client.
        
        
        String message="don't worry";
        res.put("real",response.toString());
        res.put("notification",message);
        res.put("status","completed");
        res.put("processInstanceState",this.getProcessState(processInstanceKey));

        //res="Task completed. Workflow instance key: " + response.toString();
        
            }
            else {             
              res.put("notifications","there is no job associated to this processinstacekey");
            }
       }   
       else {            
            res.put("notifications","no jobs avaiblable");
       }

       }
       
       System.out.println(res);
        return ResponseEntity.ok(res); 
    }
      catch (Exception e){
        res.put("notifications","Failed to complete task: " + e.getMessage());
        return ResponseEntity.badRequest().body(res);
      }


   }

   //todo handle non process existance
    public ResponseEntity<Object> cancelProcess(String processInstanceKey) throws NumberFormatException, OperateException {
        Map<String,Object> res =new HashMap<String , Object>();
       //cancel process instance by key
       CancelProcessInstanceResponse response = client.newCancelInstanceCommand(Long.parseLong(processInstanceKey))
                                                .send().join();
       Global.putProcessState(processInstanceKey, "CANCELED");                                          
       res.put("state","canceled");
       res.put("processInstanceState",this.getProcessState(processInstanceKey)); 

       //UPDATA PROCESS STATE
                                        
       return  ResponseEntity.ok(res);
       
       

    }

    public boolean check( Map<String, Object> taskVariables){
        if(taskVariables.get("test")=="false")
        return false;
        else return true;
    }
}
