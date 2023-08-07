package com.example.camundaCloud.workers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.impl.worker.JobClientImpl;
import io.camunda.zeebe.model.bpmn.instance.Task;
import io.camunda.zeebe.model.bpmn.instance.UserTask;
import io.camunda.zeebe.spring.client.annotation.JobWorker;
import io.camunda.zeebe.spring.client.annotation.Variable;
import io.camunda.zeebe.spring.client.annotation.ZeebeWorker;

@Component
public class Worker {
       @Autowired
   private ZeebeClient client;
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
    @JobWorker(type = "checkuid",autoComplete = true)
    public HashMap<String,Object> checkUid(@Variable String uid) throws UnirestException, JsonMappingException, JsonProcessingException{ 
        //2069735100{phone_number:response.body.phone_number,activity_score:response.body.activity_score,is_valid:response.body.is_valid,carrier:response.body.carrier,country_name:response.body.country_name}
        
       /* HttpResponse<JsonNode> response = Unirest.get("https://phone-intelligence-api.p.rapidapi.com/3.0/phone_intel?phone="+uid+"&country_hint=US")
	.header("X-RapidAPI-Key", "5565221702msh056e41e74ca8770p1f9bd6jsndeb82f6269b6")
	.header("X-RapidAPI-Host", "phone-intelligence-api.p.rapidapi.com")
	.asJson();*/

       HttpResponse<JsonNode> response = Unirest.get("https://phone-intelligence-api.p.rapidapi.com/3.0/phone_intel?phone="+uid+"0&country_hint=US")
	.header("X-RapidAPI-Key", "949a0f63d7msha8a20a4b3cfb715p17f6f6jsn19400031ff56")
	.header("X-RapidAPI-Host", "phone-intelligence-api.p.rapidapi.com")
	.asJson(); 
    
        // Create an instance of ObjectMapper from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string into a HashMap
        //String uid=task.getAttributeValue("uid");
        System.out.println("         **** checking uid: "+uid+"  ****");
        System.out.println(response.getBody().toString());
        HashMap<String,Object> variable=new HashMap<String,Object>();
        
            // Parse the JSON string into a HashMap
             variable = objectMapper.readValue(response.getBody().toString(), HashMap.class);
            variable.put("personid", "55160");
        variable.put("step","0");
            return variable; 
            
        
    }

      
        @JobWorker(type = "io.camunda.zeebe:userTask",autoComplete = false)
        public void handleJob(final ActivatedJob job) throws IOException {
       
                
               // client.newDeployResourceCommand("/forms/")
/*
                ArrayList<String> l=new ArrayList<String>();
        Process process=null;
        String command="start chrome --app=\"file:///C:/Users/guest/Desktop/camunda/camundaCloud/src/main/java/com/example/camundaCloud/index.html";

        //System.out.println("Command is "+command);
        try {ProcessBuilder builder = new ProcessBuilder( "cmd.exe", "/c",command);
        builder.redirectErrorStream(true);
        process = builder.start();
            // (commandString);
            
        } catch (IOException e) {
        } if(process!=null){  
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    l.add(line);
                    System.out.print(line+"\n");
                }}      
        


*/
        
        Map variables = job.getVariablesAsMap();
        // every time a usertask accure we save the job by the processinstancekey
        Global.currentJobs.put(job.getElementInstanceKey()+"",job);      
               
        System.out.println(job.toString()+ "job.getVariables()"+variables+" ****job handling ***"+job.getElementId());
        // get variables
        
        
        // business logic
        // ...
        // complete the tasks
        /*client.newCompleteCommand(job.getKey())
            .variables(Map.of("newVariable","VariableFromClient"))
            .send()
            .join();*/
        } 
     
    
}
