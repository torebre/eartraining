package com.kjipo.midi;

import org.junit.Test;

import javax.sound.midi.*;

public class SynthesizerTest {

    @Test
    public void synthesizerTest() throws MidiUnavailableException, InterruptedException {

        Synthesizer synthesizer = MidiSystem.getSynthesizer();

//            System.out.println(MidiSystem.getMidiDeviceInfo());

        for (MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
            System.out.println(info.getName() + ", " + info.getDescription());
        }

        synthesizer.open();

//            System.out.println(synthesizer.getDeviceInfo().getDescription());

        MidiChannel[] channels = synthesizer.getChannels();

        channels[0].noteOn(100, 60);
        Thread.sleep(20000);
        channels[0].noteOff(60);

        synthesizer.close();

    }


}
