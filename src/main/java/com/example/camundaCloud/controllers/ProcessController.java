package com.example.camundaCloud.controllers;

import java.util.HashMap;
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
import com.example.camundaCloud.services.ProcessService;

import io.camunda.zeebe.client.ZeebeClient;
//import org.camunda.bpm.engine.RuntimeService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
@RestController("/process")
public class ProcessController {
  @Autowired
  ProcessService processService;

  
   
   
   @GetMapping
   public String onBord(){
    client.newActivateJobsCommand().jobType("Test");
    return "success";
   }

   
   @Autowired
   private ZeebeClient client;
       private final static Logger LOG = LoggerFactory.getLogger(ProcessController.class);

   
  @PostMapping("/process/complete-task/{processInstancekey}")
  public ResponseEntity<Object> completeTask(@PathVariable String processInstancekey,
                                            @RequestBody Map<String, Object> taskVariables)
   { return this.processService.completeTask(processInstancekey, taskVariables);
   }
    @PostMapping("/process/start/{bpmnPnstanceId}")
    public  ResponseEntity<Object> startProcess(@PathVariable String bpmnPnstanceId) {
       return this.processService.startProcess(bpmnPnstanceId);
      }


   @GetMapping("/process/{processInstanceKey}/get-task")
   public ResponseEntity<Object> getTask(@PathVariable String processInstanceKey)
     {
       return this.processService.getTask(processInstanceKey);
     }  




   
}
