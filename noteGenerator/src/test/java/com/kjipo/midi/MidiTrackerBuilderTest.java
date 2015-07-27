package com.kjipo.midi;


import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kjipo.representation.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class MidiTrackerBuilderTest {
    private final Random randomizer = new Random();


    @Test
    public void testSplitLength() {
        Assert.assertEquals(MidiTrackBuilder.splitLengthIntoBytes(127), new byte[]{0x00, 0x00, 0x00, 127});
        Assert.assertEquals(MidiTrackBuilder.splitLengthIntoBytes(27), new byte[]{0x00, 0x00, 0x00, 27});
    }

    @Test
    public void testCreateSimpleSequence() throws IOException {
        MidiTrackBuilder midiTrackBuilder = MidiTrackBuilder.createNewInstance();
        byte midiSequence[] = midiTrackBuilder.addDelta(0)
                .setTicksPerQuarterNote(16)
                .setTempo(100000000)
                .setTimeSignature(4, 4)
                .addNoteOn(60, 127)
                .addDelta(60)
                .addNoteOff(60, 127)
                .build();
        Assert.assertTrue(midiSequence.length > 0);
    }

}
