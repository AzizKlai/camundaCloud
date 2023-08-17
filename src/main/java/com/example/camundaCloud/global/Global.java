package com.example.camundaCloud.global;

import java.util.HashMap;

import io.camunda.zeebe.client.api.response.ActivatedJob;

public class Global {
    //temprorarily store job data
    public static HashMap<String,ActivatedJob> currentJobs=new HashMap<>(); 
    public static HashMap<String,ActivatedJob> getCurrentJobs(){
        return currentJobs;
    }
    public static void putJobs(String key,ActivatedJob job){
        currentJobs.put(key, job);
    }

    public static HashMap<String,String> currentProcessState=new HashMap<>(); 
    public static HashMap<String,String> getCurrentProcessState(){
        return currentProcessState;
    }
    public static void putProcessState(String key,String state){
        currentProcessState.put(key, state);
    }
    
}
