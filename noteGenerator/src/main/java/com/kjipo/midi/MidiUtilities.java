package com.kjipo.midi;


import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.collect.SortedSetMultimap;
import com.kjipo.representation.Note;
import com.kjipo.representation.Sequence;

import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;


public final class MidiUtilities {


//    public static byte[] transformSequenceToMidiFormat(OnOffSeries<Note> notes) {
//        MidiTrackBuilder midiTrackBuilder = MidiTrackBuilder.createNewInstance();
//        Iterator<OnOffSeries.Change<Note>> itrChanges = notes.getChangeIterator();
//        int prevIndex = 0;
//        while (itrChanges.hasNext()) {
//            OnOffSeries.Change<Note> change = itrChanges.next();
//            int delta = change.getIndex() - prevIndex;
//            prevIndex = change.getIndex();
//            midiTrackBuilder.addDelta(delta);
//
//            for (Note onEvent : change.getOnEvents()) {
//                    midiTrackBuilder.addNoteOn(onEvent.getPitch(), 127);
//            }
//
//            for (Note offEvent : change.getOffEvents()) {
//                    midiTrackBuilder.addNoteOff(offEvent.getPitch(), 127);
//            }
//
//        }
//        return midiTrackBuilder.build();
//    }


    public static byte[] transformSequenceToMidiFormat(Sequence sequence) {
        MidiTrackBuilder builder = MidiTrackBuilder.createNewInstance();

        builder.setTempo(sequence.getTempoInMillisecondsPerQuarterNote());

        int currentPoint = 0;

        Multimap<Integer, Note> pendingOffEvents = MultimapBuilder.treeKeys().arrayListValues().build();


        for(Note note : sequence.getNotes()) {

            switch(note.getElementType()) {
                case BAR_LINE:
                    currentPoint = 0;

                default:
                    if(note.getStartWithinBar() < currentPoint) {
                        // We make an assumtion that the notes are ordered by
                        // increasing start points
                        throw new RuntimeException("At point: " +currentPoint +". Going backwards in: " +sequence);
                    }
                    currentPoint = note.getStartWithinBar();
            }


// TODO Handle off events
//            Map.Entry<Integer, Collection<Note>> offEvents =


            // TODO Is 0 a valid pitch?
            if(note.getPitch() >= 0) {
                builder.addNoteOn(note.getPitch(), note.getVelocity());
                pendingOffEvents.put(note.getStartWithinBar() + note.getDurationWithinBar(), note);
            }







        }


        // TODO Handle remaining off events

        return builder.build();
    }




}
