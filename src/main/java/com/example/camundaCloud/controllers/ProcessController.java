package com.example.camundaCloud.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.hc.core5.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.camundaCloud.global.Global;
import com.example.camundaCloud.services.ProcessService;

import io.camunda.operate.exception.OperateException;
import io.camunda.zeebe.client.ZeebeClient;
//import org.camunda.bpm.engine.RuntimeService;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.response.CompleteJobResponse;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;
import io.camunda.zeebe.spring.client.properties.OperateClientConfigurationProperties;
@RestController()
@RequestMapping("/process")
@CrossOrigin("*")
public class ProcessController {
  @Autowired
  private ProcessService processService;
   
  
   
   
   @GetMapping("")
   public String onBord(){
    //client.newActivateJobsCommand().jobType("Test");
    return "success";
   }

   
   @Autowired
   private ZeebeClient client;
       private final static Logger LOG = LoggerFactory.getLogger(ProcessController.class);
   
  // private OperateClientConfigurationProperties

   
  @PostMapping("/complete-task/{processInstanceKey}")
  public ResponseEntity<Object> completeTask(@PathVariable String processInstanceKey,
                                            @RequestBody Map<String, Object> taskVariables)
   {  String taskName=(String)taskVariables.get("taskName"); 
      Map<String, Object> data= (Map<String, Object>) taskVariables.get("data");
      //taskVariables.remove("taskId");
      System.out.println("from controller"+taskName);
    return this.processService.completeTask(processInstanceKey,taskName, data);
   }
    @PostMapping("/start/{bpmnProcessId}")
    public  ResponseEntity<Object> startProcess(@PathVariable String bpmnProcessId) {
       return this.processService.startProcess(bpmnProcessId);
      }


   @GetMapping("/{processInstanceKey}/get-task")
   public ResponseEntity<Object> getTask(@PathVariable String processInstanceKey)
     {
       return this.processService.getTask(processInstanceKey);
     }  
   @PostMapping("/cancel-task/{processInstanceKey}")  
   public ResponseEntity<Object> cancelProcess(@PathVariable String processInstanceKey) throws NumberFormatException, OperateException
   {
       return this.processService.cancelProcess(processInstanceKey);
   }
   @GetMapping("/get-state/{processInstanceKey}")
   public ResponseEntity<Object> getProcessStateOperate(@PathVariable String processInstanceKey){
    return this.processService.getProcessStateOperate(processInstanceKey);
   }




   
}
