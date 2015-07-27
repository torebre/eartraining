package com.kjipo.representation;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.testng.annotations.Test;

import java.io.*;
import java.util.*;


public class SequenceSerializing {
    public static final int MILLISECONDS_PER_QUARTER_NOTE = 1000;


    @Test
    public void serializeSequenceTest() throws IOException {
        Sequence sequence = createTestSequence();

        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(output, sequence);

        String outputString = new String(output.toByteArray());

        System.out.println(outputString);

//        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/workspace/EarTrainingAndroid/noteVisualizer/src/js/test_output.json")));

        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(new File("/home/student/test_output2.json")));
        bufferedOutputStream.write(output.toByteArray());
        bufferedOutputStream.close();
    }


    public static Sequence createTestSequence() {
        EventSeries eventSeries = EventSeriesFactoryProvider.getEventSeriesFactory().createEmptySeries();
        Map<Event, Note> eventSeqMapping = new HashMap<Event, Note>();
        ElementType[] validElements = new ElementType[] {ElementType.QUARTERNOTE, ElementType.HALFNOTE};
        List<Note> notes = new ArrayList<Note>();
        int cumulativeDurationWithinBar = 0;
        int id = 0;
        Random randomizer = new Random();

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
            Event event = eventSeries.addEvent(i, duration);
            Note note = new Note(id++, pitch, cumulativeDurationWithinBar, type, duration);
            cumulativeDurationWithinBar += duration;
            notes.add(note);

            if(cumulativeDurationWithinBar % 4 == 0) {
                notes.add(new Note(id++, -1, 0, ElementType.BAR_LINE, duration));
                cumulativeDurationWithinBar = 0;
            }

            eventSeqMapping.put(event, note);
        }

        // TODO The duration of bar parameter should be calculated
        return new Sequence(ClefType.TREBLE, 4, 4, notes, 4, MILLISECONDS_PER_QUARTER_NOTE);
    }


}
