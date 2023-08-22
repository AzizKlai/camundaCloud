package com.example.camundaCloud.workers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;

@Component
// a public class made to contain all workers methods 
public class Worker {


    //store job data
    public static HashMap<String,ActivatedJob> currentJobs=new HashMap<>(); 
    public static HashMap<String,ActivatedJob> getCurrentJobs(){
        return currentJobs;
    }
    /**
     * save currentjob by processInstanceKey
     * @param key processInstanceKey
     * @param job
     */
    public static void putJobs(String key,ActivatedJob job){
        currentJobs.put(key, job);
    }
    //store processstate
    public static HashMap<String,String> currentProcessState=new HashMap<>(); 
    public static HashMap<String,String> getCurrentProcessState(){
        return currentProcessState;
    }
    /**
     * 
     * @param key  processInstanceKey
     * @param state
     */
    public static void putProcessState(String key,String state){
        currentProcessState.put(key, state);
    }





    // JobWorders:

    // jobworker with type verify-id associated to service tasks with that tasktype made to
    @JobWorker(type = "verify-id", autoComplete = true) 
    public void verfier(){
        System.out.println("**** verification on ****");
    }

    /**
     * @param uid this uid is retrived from the processInstance variables with name uid
     * @return
     * @throws UnirestException 
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @JobWorker (type = "checkuid",autoComplete = true)
    public HashMap<String,Object> checkUid(@Variable String uid) throws UnirestException, JsonMappingException, JsonProcessingException{ 
        HashMap<String,Object> variable=new HashMap<String,Object>();
        variable.put("personid", "55160");
        variable.put("step","0");
        variable.put("is_valid",true);
            //when we return map the worker saves these variables to with the current task that triggred this jobWorker
            return variable; 
        
    }


    
        /**
         * 
         * @param Job retrive automatically the activated job information
         * @throws IOException
         */
        @JobWorker(type = "io.camunda.zeebe:userTask",autoComplete = false)
        public void handleUserTask(final ActivatedJob Job) throws IOException{
            handleJob(Job);
        }

         /**
          * this job worker is make to endprocess tasks that should came in the end of
          the process with tasktype endProcess is important to tell that the processInstance is completed
          * @param job
          * @throws IOException
          */
         @JobWorker(type = "endProcess",autoComplete = true)
        public void handleEnd(final ActivatedJob job) throws IOException{
        handleJob(job);
        Worker.putProcessState(job.getProcessInstanceKey()+"", "COMPLETED"); 
        }

        // this handle function to handle saving jobs
        /**
         * made to save informations of every launched job wich contains key variables and processInstanceKey
         * @param job
         * @throws IOException
         */
        public void handleJob(final ActivatedJob job) throws IOException {
        //Map variables = job.getVariablesAsMap();
        // every time a usertask accure we save the job by the processinstancekey
        Worker.putJobs(job.getProcessInstanceKey()+"",job);  
        Worker.putProcessState(job.getProcessInstanceKey()+"", "ACTIVE"); 
        //System.out.println("\n \n");    
        //System.out.println(job.toString()+" ****job handling ***"+job.getElementId());
        

        // business logic
        // ...
        // complete the tasks
        /*client.newCompleteCommand(job.getKey())
            .variables(Map.of("newVariable","VariableFromClient"))
            .send()
            .join();*/
        } 
     
    
}
