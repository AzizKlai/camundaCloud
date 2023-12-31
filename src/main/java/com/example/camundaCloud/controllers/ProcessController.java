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

   /**
    * 
    * @param bpmnProcessId 
    * @return
    */
   @PostMapping("/start/{bpmnProcessId}")
    public  ResponseEntity<Object> startProcess(@PathVariable String bpmnProcessId) {
       return this.processService.startProcess(bpmnProcessId);
      }
    /**
     * complete an active specific task of processinstance 
     * @param processInstanceKey
     * @param taskName  
     * @param taskVariables from body of request
     * @return
     */  
  @PostMapping("/{processInstanceKey}/complete-task/{taskName}")
  public ResponseEntity<Object> completeTask(@PathVariable String processInstanceKey,@PathVariable String taskName,
                                            @RequestBody Map<String, Object> taskVariables)
     { 
    return this.processService.completeTask(processInstanceKey,taskName, taskVariables);
   }
    

   /**
    * 
    * @param processInstanceKey
    * @return
    */
   @GetMapping("/{processInstanceKey}/get-task")
   public ResponseEntity<Object> getTask(@PathVariable String processInstanceKey)
     {
       return this.processService.getTask(processInstanceKey);
     }  

     /**
      * 
      * @param processInstanceKey
      * @return
      * @throws NumberFormatException
      * @throws OperateException
      */
   @PostMapping("/{processInstanceKey}/cancel-task")  
   public ResponseEntity<Object> cancelProcess(@PathVariable String processInstanceKey) throws NumberFormatException, OperateException
   {
       return this.processService.cancelProcess(processInstanceKey);
   }


   /**
    * 
    * @param processInstanceKey
    * @return
    */
   @GetMapping("/{processInstanceKey}/get-state")
   public ResponseEntity<Object> getProcessState(@PathVariable String processInstanceKey){
    return this.processService.getProcessState(processInstanceKey);
   }




   
}
