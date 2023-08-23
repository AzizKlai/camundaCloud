package com.example.camundaCloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.camunda.zeebe.spring.client.annotation.Deployment;


@SpringBootApplication
@Deployment(resources = {
    "classpath:test.bpmn","classpath:processV3.bpmn","classpath:processV1.bpmn",
     "classpath:process4.bpmn"})
public class Application  {
    public static void main(String... args){
     SpringApplication.run(Application.class, args);
    }
           
    
    

   

}
