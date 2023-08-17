package com.example.camundaCloud.workers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.camundaCloud.global.Global;
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
public class Worker {
       
    @JobWorker(type = "verify-id", autoComplete = true)
    public void verfier(){
        System.out.println("**** verification on ****");
    }
    /**
     * @param uid
     * @return
     * @throws UnirestException
     * @throws JsonProcessingException
     * @throws JsonMappingException
     */
    @JobWorker (type = "checkuid",autoComplete = true)
    public HashMap<String,Object> checkUid(@Variable String uid) throws UnirestException, JsonMappingException, JsonProcessingException{ 
        //2069735100{phone_number:response.body.phone_number,activity_score:response.body.activity_score,is_valid:response.body.is_valid,carrier:response.body.carrier,country_name:response.body.country_name}
        
       
       //this is a get call to the rapid api endpoint (for testing) that return is_valid true if the uid(is a valid phone number) with related variables else returns is_valid false
       HttpResponse<JsonNode> response = Unirest.get("https://phone-intelligence-api.p.rapidapi.com/3.0/phone_intel?phone="+uid+"0&country_hint=US")
	   .header("X-RapidAPI-Key", "949a0f63d7msha8a20a4b3cfb715p17f6f6jsn19400031ff56")
	   .header("X-RapidAPI-Host", "phone-intelligence-api.p.rapidapi.com")
	   .asJson(); 
    
        // Create an instance of ObjectMapper from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string into a HashMap
        //String uid=task.getAttributeValue("uid");
        System.out.println(response.getBody().toString());
        HashMap<String,Object> variable=new HashMap<String,Object>();
        
            // Parse the JSON string into a HashMap
             variable = objectMapper.readValue(response.getBody().toString(), HashMap.class);
            variable.put("personid", "55160");
            variable.put("step","0");
            //when we return map the worker saves these variables to with the current task that triggred this jobWorker
            return variable; 
        
    }


    

        @JobWorker(type = "io.camunda.zeebe:userTask",autoComplete = false)
        public void handleUserTask(final ActivatedJob Job) throws IOException{
            handleJob(Job);
        }


         @JobWorker(type = "endProcess",autoComplete = true)
        public void handleEnd(final ActivatedJob job) throws IOException{
        handleJob(job);
        Global.putProcessState(job.getProcessInstanceKey()+"", "COMPLETED"); 
        }

        // this handle function to handle saving jobs
        public void handleJob(final ActivatedJob job) throws IOException {
        Map variables = job.getVariablesAsMap();
        // every time a usertask accure we save the job by the processinstancekey
        Global.putJobs(job.getProcessInstanceKey()+"",job);  
        Global.putProcessState(job.getProcessInstanceKey()+"", "ACTIVE"); 
        System.out.println("\n \n");    
        System.out.println(job.toString()+ "job.getVariables()"+variables+" ****job handling ***"+job.getElementId());
        

        // business logic
        // ...
        // complete the tasks
        /*client.newCompleteCommand(job.getKey())
            .variables(Map.of("newVariable","VariableFromClient"))
            .send()
            .join();*/
        } 
     
    
}
