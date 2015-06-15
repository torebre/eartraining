package com.kjipo.representation;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.google.common.collect.Lists;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class DummyData {
    private static final int numberOfPoints = 1000;


    private static void createDummyData(File file) {
        List<DataGeneratorInterface> dataGenerators = new ArrayList<DataGeneratorInterface>();
        dataGenerators.add(new DataGenerator());



        try {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file));
            JsonFactory factory = new JsonFactory();
            com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(bufferedOutputStream, JsonEncoding.UTF8);

            for (DataGeneratorInterface dataGenerator : dataGenerators) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("parameter", dataGenerator.getParameterName());
                jsonGenerator.writeFieldName("values");
                jsonGenerator.writeStartArray();
                for (int row = 0; row < numberOfPoints; ++row) {
                    jsonGenerator.writeNumber(dataGenerator.getNextValue());
                }
                jsonGenerator.writeEndArray();
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private interface DataGeneratorInterface {

        public String getParameterName();

        public double getNextValue();


    }


    private static class DataGenerator implements DataGeneratorInterface {
        private double currentValue = -0.01;
        private double increment = 0.01;


        public DataGenerator() {

        }

        @Override
        public String getParameterName() {
            return "Sine wave parameter";
        }

        @Override
        public double getNextValue() {
            currentValue += increment;
            return Math.sin(currentValue);
        }
    }


    public static void main(String args[]) {
        createDummyData(new File("/home/student/testParameter.json"));


    }


}
