package com.kjipo.representation;



import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import java.util.Collections;


public class SequenceGenerator {


//    public static EventSeries generateSequence(int numberOfNotes) {
//        EventSeries timeSeries = EventSeriesFactoryProvider.getEventSeriesFactory().createEmptySeries();
//        Event event = null;
//
//        for (int i = 0; i < numberOfNotes; ++i) {
//            timeSeries.addEvent(i, (int)Math.ceil(Math.random() * 10));
//        }
//
//        return timeSeries;
//    }
//
//
//    private static int generateRandomPitch() {
//        return 60 + (int)Math.ceil(Math.random() * 20);
//    }




    public static void main(String[] args)
    {
        try {
            Synthesizer synthesizer = MidiSystem.getSynthesizer();

//            System.out.println(MidiSystem.getMidiDeviceInfo());

            for(MidiDevice.Info info : MidiSystem.getMidiDeviceInfo()) {
                System.out.println(info.getName() +", " +info.getDescription());
            }

            synthesizer.open();

//            System.out.println(synthesizer.getDeviceInfo().getDescription());

            MidiChannel[] channels = synthesizer.getChannels();

            channels[0].noteOn(100, 60);
            Thread.sleep(20000);
            channels[0].noteOff(60);

            synthesizer.close();
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }


}
