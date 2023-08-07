package com.example.camundaCloud;

import java.io.File;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.camundaCloud.global.Global;

import io.camunda.operate.dto.ProcessInstance;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.response.ProcessInstanceEvent;
import io.camunda.zeebe.spring.client.annotation.Deployment;


@SpringBootApplication
@Deployment(resources = {"classpath:test.bpmn","classpath:processV3.bpmn","classpath:processV1.bpmn","classpath:processV2.bpmn" , "classpath:process4.bpmn", "classpath:service.bpmn"})
public class Application  {
    public static void main(String... args){
          String filepath="process4.bpmn";
          File inputFile=new File(filepath);
          
          // initiate the global map to a new one
          Global.currentJobs=new HashMap<>();


           SpringApplication.run(Application.class, args);
    }
           
    
    

   

}
