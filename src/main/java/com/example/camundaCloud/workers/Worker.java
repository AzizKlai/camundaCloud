package com.example.camundaCloud.workers;

import java.io.IOException;
import java.util.HashMap;

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
import io.camunda.zeebe.model.bpmn.instance.Task;
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
        
        HttpResponse<JsonNode> response = Unirest.get("https://phone-intelligence-api.p.rapidapi.com/3.0/phone_intel?phone="+uid+"&country_hint=US")
	.header("X-RapidAPI-Key", "5565221702msh056e41e74ca8770p1f9bd6jsndeb82f6269b6")
	.header("X-RapidAPI-Host", "phone-intelligence-api.p.rapidapi.com")
	.asJson();
    
        // Create an instance of ObjectMapper from Jackson library
        ObjectMapper objectMapper = new ObjectMapper();

        // Parse the JSON string into a HashMap
        //String uid=task.getAttributeValue("uid");
        System.out.println("         **** checking uid: "+uid+"  ****");
        System.out.println(response.getBody().toString());
        HashMap<String,Object> variable=new HashMap<String,Object>();
        variable.put("personid", "55160");
            // Parse the JSON string into a HashMap
             variable = objectMapper.readValue(response.getBody().toString(), HashMap.class);
            
            return variable; 
            
        
    }
    
}
