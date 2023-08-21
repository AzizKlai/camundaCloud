package com.example.camundaCloud.global;

import java.util.HashMap;

import io.camunda.zeebe.client.api.response.ActivatedJob;

public class Global {
    //temprorarily store job data
    public static HashMap<String,ActivatedJob> currentJobs=new HashMap<>(); 
    public static HashMap<String,ActivatedJob> getCurrentJobs(){
        return currentJobs;
    }
    /**
     * save ev
     * @param key processInstanceKey
     * @param job
     */
    public static void putJobs(String key,ActivatedJob job){
        currentJobs.put(key, job);
    }
    //temprorarily store processstate
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
    
}
