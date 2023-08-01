package com.example.camundaCloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.operate.dto.ProcessInstance;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.Deployment;


@SpringBootApplication
@Deployment(resources = {"classpath:processV1.bpmn","classpath:processV2.bpmn" , "classpath:process4.bpmn", "classpath:service.bpmn"})
public class Application  {
    public static void main(String... args){
           SpringApplication.run(Application.class, args);
    }

    
    

   

}
