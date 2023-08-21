package com.example.camundaCloud.controllers;

import java.util.Map;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.camundaCloud.services.ProcessService;

import io.camunda.operate.exception.OperateException;

@RestController()
@RequestMapping("/process")
@CrossOrigin("*")
public class ProcessController {
  
  @Autowired
  private ProcessService processService;
  private final static Logger LOG = LoggerFactory.getLogger(ProcessController.class);

   
   @GetMapping("")
   public String onBord(){
    return "success";
   }

  
   @PostMapping("/start/{bpmnProcessId}")
    public  ResponseEntity<Object> startProcess(@PathVariable String bpmnProcessId) {
       return this.processService.startProcess(bpmnProcessId);
      }
      
  @PostMapping("/{processInstanceKey}/complete-task/{taskName}")
  public ResponseEntity<Object> completeTask(@PathVariable String processInstanceKey,@PathVariable String taskName,
                                            @RequestBody Map<String, Object> taskVariables)
     { System.out.println("from controller"+taskName);
    return this.processService.completeTask(processInstanceKey,taskName, taskVariables);
   }
    


   @GetMapping("/{processInstanceKey}/get-task")
   public ResponseEntity<Object> getTask(@PathVariable String processInstanceKey)
     {
       return this.processService.getTask(processInstanceKey);
     }  
   @PostMapping("/{processInstanceKey}/cancel-task")  
   public ResponseEntity<Object> cancelProcess(@PathVariable String processInstanceKey) throws NumberFormatException, OperateException
   {
       return this.processService.cancelProcess(processInstanceKey);
   }
   @GetMapping("/{processInstanceKey}/get-state")
   public ResponseEntity<Object> getProcessStateOperate(@PathVariable String processInstanceKey){
    return this.processService.getProcessStateOperate(processInstanceKey);
   }




   
}
