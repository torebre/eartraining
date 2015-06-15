package com.kjipo.representation;


import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.kjipo.utilities.TransformUtilities;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Contains utilities for converting a note sequence to
 * a layout format the note visualizer can easily use.
 *
 */
public final class SequenceToJson {



    public static void transformSequenceToJson(EventSeries timeSeries, Map<Event, SequenceElement> eventElementMap, OutputStream output) throws IOException {
        JsonFactory factory = new JsonFactory();
        com.fasterxml.jackson.core.JsonGenerator jsonGenerator = factory.createGenerator(output, JsonEncoding.UTF8);
        jsonGenerator.setPrettyPrinter(new DefaultPrettyPrinter());
        OnOffSeries onOffSeries = TransformUtilities.transformToSingleEventLine(timeSeries);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeFieldName("noteSequence");
        jsonGenerator.writeStartArray();

        Iterator<OnOffSeries.Change> changeIterator = onOffSeries.getChangeIterator();
        Map<Event, Integer> eventIdMap = new HashMap<Event, Integer>();
        int idCounter = 0;

        while(changeIterator.hasNext()) {
            jsonGenerator.writeStartObject();

            OnOffSeries.Change change = changeIterator.next();
            Set<Event> onEvents = change.getOnEvents();

            for(Event event : onEvents) {
                eventIdMap.put(event, idCounter++);
            }

            jsonGenerator.writeFieldName("onEvents");
            jsonGenerator.writeStartArray();
            for(Event event : onEvents) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeFieldName("id");
                jsonGenerator.writeNumber(eventIdMap.get(event));

                SequenceElement element = eventElementMap.get(event);

                if(element.getElementType().isNote) {
                    writeNote((Note)element, jsonGenerator);
                }
                else {
                    // TODO
                    throw new IllegalArgumentException("Nothing else than notes supported yet");
                }
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

//            Set<Event> offEvents = change.getOffEvents();
//            jsonGenerator.writeFieldName("offEvents");
//            jsonGenerator.writeStartArray();
//            for(Event event : offEvents) {
//                jsonGenerator.writeNumber(eventIdMap.get(event));
//            }
//            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }

        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();


        jsonGenerator.close();

    }



    private static void writeNote(Note note, JsonGenerator jsonGenerator) throws IOException {
        jsonGenerator.writeFieldName("pitch");
        jsonGenerator.writeNumber(note.getPitch());


    }


}
