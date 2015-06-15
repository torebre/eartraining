package com.kjipo.representation;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.kjipo.representation.EventSeriesFactory;
import com.kjipo.representation.EventSeries;
import com.kjipo.representation.OnOffSeries;
import com.kjipo.utilities.TransformUtilities;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;


public class SequenceToJsonTest {
    private final Random randomizer = new Random();




    @Test
    public void simpleSequenceToJson() throws IOException {
        EventSeries eventSeries = EventSeriesFactory.getEventSeriesFactory().createEmptySeries();
        Map<Event, SequenceElement> eventSeqMapping = new HashMap<Event, SequenceElement>();
        ElementType[] validElements = new ElementType[] {ElementType.QUARTERNOTE, ElementType.HALFNOTE};

        for (int i = 0; i < 10; ++i) {
            ElementType type = validElements[randomizer.nextInt(validElements.length)];
            int pitch = 60 + randomizer.nextInt(11);

            Event event = eventSeries.addEvent(i, type.duration);
            Note note = new Note(type, pitch);

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


        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output.json")));
        bufferedOutputStream.write(outputStream.toByteArray());
        bufferedOutputStream.close();



    }




}
