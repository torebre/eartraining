package com.kjipo.representation;


import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class SequenceToJsonTest {
    private final Random randomizer = new Random();




    @Test
    public void simpleSequenceToJson() throws IOException {
        EventSeries eventSeries = EventSeriesFactoryProvider.getEventSeriesFactory().createEmptySeries();
        Map<Event, Note> eventSeqMapping = new HashMap<Event, Note>();
        ElementType[] validElements = new ElementType[] {ElementType.QUARTERNOTE, ElementType.HALFNOTE};
        int cumulativeDuration = 0;
        int id = 0;

        for (int i = 0; i < 10; ++i) {
            ElementType type = validElements[randomizer.nextInt(validElements.length)];
            int pitch = 60 + randomizer.nextInt(11);
            int duration = -1;
            switch (type) {
                case QUARTERNOTE:
                    duration = 1;
                    break;

                case HALFNOTE:
                    duration = 2;
                    break;
            }
            cumulativeDuration += duration;
            Event event = eventSeries.addEvent(i, duration);
            Note note = new Note(id++, pitch, cumulativeDuration, type, duration);

            eventSeqMapping.put(event, note);
        }


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output.json")));
        SequenceToJson.transformSequenceToJson(eventSeries, eventSeqMapping, outputStream);

        System.out.println((new String(outputStream.toByteArray())));

        JsonFactory factory = new JsonFactory();

        JsonParser parser = factory.createParser(outputStream.toByteArray());

        JsonToken jsonToken;
        while((jsonToken = parser.nextToken()) != null);


        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output2.json")));
        bufferedOutputStream.write(outputStream.toByteArray());
        bufferedOutputStream.close();



    }




}
