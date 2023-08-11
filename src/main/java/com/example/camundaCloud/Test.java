package com.example.camundaCloud;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.camundaCloud.global.Global;
import com.example.camundaCloud.services.ProcessService;

import io.camunda.operate.CamundaOperateClient;
import io.camunda.operate.auth.SimpleAuthentication;
import io.camunda.operate.dto.ProcessInstance;
import io.camunda.operate.dto.ProcessInstanceState;
import io.camunda.operate.dto.SearchResult;
import io.camunda.operate.exception.OperateException;
import io.camunda.operate.search.DateFilter;
import io.camunda.operate.search.DateFilterRange;
import io.camunda.operate.search.ProcessInstanceFilter;
import io.camunda.operate.search.SearchQuery;
import io.camunda.zeebe.client.ZeebeClient;
import io.camunda.zeebe.client.api.command.ModifyProcessInstanceCommandStep1;
import io.camunda.zeebe.client.api.response.ProcessInstanceResult;

public class Test {
    //private static CamundaOperateClient client;
    public static void main (String args[]) throws OperateException{
        //zeebeClient=new ZeebeClient();
        SimpleAuthentication sa = new SimpleAuthentication("demo", "demo", "http://localhost:8081");
        CamundaOperateClient client = new CamundaOperateClient.Builder().operateUrl("http://localhost:8081").authentication(sa).build();
        
        //search process instances based on filters
        ProcessInstanceFilter instanceFilter = new ProcessInstanceFilter.Builder().bpmnProcessId("Process_test").build();
        SearchQuery instanceQuery = new SearchQuery.Builder().filter(instanceFilter).size(20).build();

        List<ProcessInstance> list = client.searchProcessInstances(instanceQuery);

        SearchResult<ProcessInstance> result = client.searchProcessInstanceResults(instanceQuery);
            
        //get a process instance by its key
        String key="2251799813772644";

        ProcessInstance instance = client.getProcessInstance(Long.parseLong(key) );
        
        System.out.println(list);
        
        System.out.println("\n");
        System.out.println(result);
        System.out.println("\n");
        System.out.println(instance.getState()+"instance"+instance.getParentKey());
        //ProcessInstanceState
        System.out.println(Global.getCurrentJobs());
        //Global.currentJobs=new HashMap<>();
        System.out.println(Global.getCurrentJobs());

    }
}
