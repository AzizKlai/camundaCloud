package com.example.camundaCloud.global;

import java.util.HashMap;

import io.camunda.zeebe.client.api.response.ActivatedJob;

public class Global {
    public static HashMap<String,ActivatedJob> currentJobs=new HashMap<>(); 
    public static HashMap<String,ActivatedJob> getCurrentJobs(){
        return currentJobs;
    }
    public static void putJobs(String key,ActivatedJob job){
        currentJobs.put(key, job);
    }
    
}
