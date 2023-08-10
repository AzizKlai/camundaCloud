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
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
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
     

     public String getProcessState(String processInstanceKey) throws NumberFormatException, OperateException{
        try{
     return this.operateClient.getProcessInstance(Long.parseLong(processInstanceKey)).getState().toString();
        }
        catch(Exception e){ return "doesn't exist";}
    }


     public ResponseEntity<Object> startProcess(String bpmnPnstanceId) {
        // TODO Auto-generated method stub
        try{
            Map<String,Object> res =new HashMap<String , Object>();
        final ProcessInstanceEvent event= client.newCreateInstanceCommand()
        .bpmnProcessId(bpmnPnstanceId)
        .latestVersion()
        .send()
        .join();
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
        try{ 
            Map<String,Object> res =new HashMap<String , Object>();
            res.put("processInstanceState",this.getProcessState(processInstanceKey));
            HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
            // get the formkey from the job 
            ActivatedJob instanceTask=jobs.get(processInstanceKey);
            //.get(processInstanceKey).toString();
            // Check if the job (task) is available for processing
            if (jobs != null && instanceTask!=null) {
                res.put("formId",instanceTask.getCustomHeaders().get("io.camunda.zeebe:formKey"));
                res.put("task",instanceTask.getElementId());
                
            } else {
                throw new Exception("No tasks available.");
            }
                    System.out.println(res);
                    return ResponseEntity.ok(res);

                }
                catch (Exception e){
                    return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to get task: " + e.getMessage());
                
                }
            }  


     public ResponseEntity<Object> completeTask(@PathVariable String processInstanceKey,
                                            @RequestBody Map<String, Object> taskVariables)
     { try{
       Map<String,Object> res =new HashMap<String , Object>();

       // getting taskinstacekey
       //todo remember to handle error in case of non process existance
       HashMap<String,ActivatedJob> jobs=Global.getCurrentJobs();
       if(jobs!=null) {
        // get the formkey from the job 
            ActivatedJob instanceTask=jobs.get(processInstanceKey);
            if(instanceTask!=null){
            
       CompleteJobResponse response = client.newCompleteCommand(instanceTask.getKey())
        .variables(taskVariables).send()
                .join();

        res.put("status","completed");
        res.put("processInstanceState",this.getProcessState(processInstanceKey));

        //res="Task completed. Workflow instance key: " + response.toString();
        
            }
            else {
                throw new Exception("there is no job associated to this processinstacekey");
            }
       }   
       else {
        throw new Exception("no jobs avaiblable");
       }
       
       System.out.println(res);
        return ResponseEntity.ok(res); 
    }
      catch (Exception e){
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to complete task: " + e.getMessage());
    
      }


   }

   //todo handle non process existance
    public ResponseEntity<Object> cancelProcess(String processInstanceKey) throws NumberFormatException, OperateException {
        Map<String,Object> res =new HashMap<String , Object>();
       //cancel process instance by key
       CancelProcessInstanceResponse response = client.newCancelInstanceCommand(Long.parseLong(processInstanceKey))
                                                .send().join();
       res.put("state","canceled");
       res.put("processInstanceState",this.getProcessState(processInstanceKey));                                         
       return  ResponseEntity.ok(res);                                       

    }
}
