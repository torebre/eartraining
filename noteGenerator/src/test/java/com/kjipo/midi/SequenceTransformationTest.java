package com.kjipo.midi;

import com.kjipo.representation.Sequence;
import com.kjipo.representation.SequenceSerializing;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Arrays;


public class SequenceTransformationTest {


    @Test
    public void encodeSequenceTest() throws IOException {
        Sequence sequence = SequenceSerializing.createTestSequence();
        byte midiData[] = MidiUtilities.transformSequenceToMidiFormat(sequence);

        byte tempo[] = MidiTrackBuilder.splitLengthIntoBytes(sequence.getTempoInMillisecondsPerQuarterNote());

        int counter = 0;
        for(byte b : midiData) {
            System.out.print(b +" ");
            ++counter;
            if(counter % 10 == 0) {
                System.out.println();
            }

        }


        // TODO Check why the start is at 22

        Assert.assertEquals(Arrays.copyOfRange(midiData, 22, 29), new byte[] {
                // Tempo
                0x00, (byte) 0xFF, 0x51, 0x03, tempo[1], tempo[2], tempo[3]});



    }



}
