package com.kjipo;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class Bridge {


    public String getData() throws JsonProcessingException {
        TestDataReader dataReader = new TestDataReader();

        System.out.println("Bridge is called");

        List<SampleData> list = dataReader.readTestData();

        return new ObjectMapper().writeValueAsString(list);

    }




}