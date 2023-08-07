package com.example.camundaCloud.controllers;

import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.camundaCloud.global.Global;

import io.camunda.zeebe.client.ZeebeClient;
//import org.camunda.bpm.engine.RuntimeService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
@RestController
public class FirstControoller {
   
   
   @GetMapping
   public String onBord(){
    client.newActivateJobsCommand().jobType("Test");
    return "success";
   }

   
   @Autowired
   private ZeebeClient client;
       private final static Logger LOG = LoggerFactory.getLogger(FirstControoller.class);

   
@PostMapping("/process/complete-task/{processInstancekey}")
  public ResponseEntity<String> completeTask(@PathVariable String processInstancekey,
                                            @RequestBody Map<String, Object> taskVariables)
   {try{
       // getting taskinstacekey
       //todo remember to handle error in case of non process existance
       Long taskInstancekey=Global.currentJobs.get(processInstancekey).getKey();
       CompleteJobResponse response = client.newCompleteCommand(taskInstancekey)
        .variables(taskVariables).send()
                .join();
        String res="Task completed. Workflow instance key: " + response.toString();
        System.out.println(res);
        return ResponseEntity.ok(res); 
   


    }
      catch (Exception e){
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to complete task: " + e.getMessage());
    
      }


   }
    @PostMapping("/process/start/{bpmnPnstanceId}")
    public  ResponseEntity<String> startProcess(@PathVariable String bpmnPnstanceId) {
        // TODO Auto-generated method stub
        try{
        final ProcessInstanceEvent event=
        client.newCreateInstanceCommand()
        .bpmnProcessId(bpmnPnstanceId)
        .latestVersion()
        .send()
        .join();
        String taskKey =event.getBpmnProcessId();
        //client.newCompleteCommand(event.)
        String res="Started instance for processDefinitionKey={"+event.getProcessDefinitionKey()+"}, bpmnProcessId={"+event.getBpmnProcessId()+"}, version={"+event.getVersion()+"} with processInstanceKey={"+ event.getProcessInstanceKey()+" with processbmpninstaceid: "+ taskKey +"}";   
        LOG.info(res);
       // client.newCompleteCommand()
        return ResponseEntity.ok(res);
        
       } catch (Exception e) {
         return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to start process: " + e.getMessage());
       }
     
      }


       @GetMapping("/process/{processInstanceKey}/get-task")// /{taskId}")
  public ResponseEntity<String> completeTask(@PathVariable String processInstanceKey// @PathVariable String taskId,
     )
     {
   try{
      // client.newCompleteCommand(null)

       // long taskInstanceKey = /* Provide the task instance key here */;
       //List<ActivatedJobEvent> activatedJobs = 
         
//getting current task by processinstancekey
//todo remember to handle error
String task=Global.currentJobs.get(processInstanceKey).toString();
// Check if the job (task) is available for processing
String res;
if (task != null) {
    res="Task Instance : " + task;
    
} else {
    res="No tasks available.";
}

       /*  CompleteJobResponse response = client.newCompleteCommand(Long.parseLong(processInstanceId))
        .variables(taskVariables).send()
                .join();
        String res="Task completed. Workflow instance key: " + response.toString();
      */  System.out.println(res);
        return ResponseEntity.ok(res);

      }
      catch (Exception e){
        return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Failed to complete task: " + e.getMessage());
    
      }
}  




  // @JobWorker(type = "io.camunda.zeebe:userTask")
//public void handleJob(final JobClient client){//}, final ActivatedJob job) {
  // Element Id
  //System.out.println(job.getElementId());
  // get variables
  //Map variables = job.getVariablesAsMap();
  // business logic
  // ...
  // complete the tasks
  //client.newCompleteCommand(job.getKey())
    //  .variables(Map.of("newVariable","VariableFromClient"))
      //.send()
      //.join();
//}
   
}
