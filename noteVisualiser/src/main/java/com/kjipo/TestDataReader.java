package com.kjipo;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.bean.ColumnPositionMappingStrategy;
import au.com.bytecode.opencsv.bean.CsvToBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by student on 10/14/14.
 */
public class TestDataReader {


    public List<SampleData> readTestData() {
        CSVReader reader = new CSVReader(new InputStreamReader(getClass().getResourceAsStream("/sample-data.csv")));
        ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
        strat.setType(SampleData.class);
        String[] columns = new String[] {"name", "value"};
        strat.setColumnMapping(columns);

        CsvToBean<SampleData> csv = new CsvToBean();
        List<SampleData> list = csv.parse(strat, reader);


        return list;
    }



    public static void main(String args[]) throws JsonProcessingException {
        TestDataReader dataReader = new TestDataReader();

        List<SampleData> list = dataReader.readTestData();

        System.out.println(new ObjectMapper().writeValueAsString(list));



    }


}
