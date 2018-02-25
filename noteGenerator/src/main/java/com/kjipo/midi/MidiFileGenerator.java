package com.kjipo.midi;

import java.io.*;


public class MidiFileGenerator {
    private static final int[] END_OF_TRACK = new int[] {0x01, 0xFF, 0x2F, 0x00};



    private void writeHeader(OutputStream outputStream) throws IOException {
        // <Header Chunk> = <chunk type><length><format><ntrks><division>
        outputStream.write(new byte[]{
                0x4d, 0x54, 0x68, 0x64, 0x00, 0x00, 0x00, 0x06,
                0x00, 0x00, // single-track format
                0x00, 0x01, // one track
                0x00, 0x10, // 16 ticks per quarter
                0x4d, 0x54, 0x72, 0x6B
        });

    }


    private static void writeIntegersAsBytes(int buffer[], OutputStream outputStream) throws IOException {
        byte byteBuffer[] = new byte[buffer.length];
        int counter = 0;
        for(int i : buffer) {
            byteBuffer[counter++] = (byte)i;
        }
        outputStream.write(byteBuffer);
    }




    public void createMidiData(OutputStream outputStream) throws IOException {
        writeHeader(outputStream);

//        writeIntegersAsBytes(new int[] {0x4D, 0x54, 0x72, 0x6B}, outputStream);

        //<Track Chunk> = <chunk type><length><MTrk event>+

        //<MTrk event> = <delta-time><event>


        //<event> = <MIDI event> | <sysex event> | <meta-event>

//        int length = 20;
        int length = 12;

        writeIntegersAsBytes(new int[] {0x00, 0x00, 0x00, length}, outputStream);

//        int timeSignature[] = new int[] {0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08};
//        writeIntegersAsBytes(timeSignature, outputStream);

//        int tempo[] = new int[] {0xFF, 0x51, 0x03, 0x07, 0xA1, 0x20};
//        writeIntegersAsBytes(tempo, outputStream);

        int buffer[] = {
                0,
                0x90,
                60,
                127
        };
        writeIntegersAsBytes(buffer, outputStream);

        buffer = new int[] {
                32,
                0x80,
                60,
                0
        };
        writeIntegersAsBytes(buffer, outputStream);

        int footer[] = new int[] {0x01, 0xFF, 0x2F, 0x00};
        writeIntegersAsBytes(footer, outputStream);

    }


    private static void test1() throws IOException {
        FileOutputStream fos = new FileOutputStream(new File("/home/student/testMidi2.mid"));
int data[] = new int[]{
        0x4D, 0x54, 0x68, 0x64, //MThd
        0x00, 0x00, 0x00, 0x06c, //chunk length
        0x00, 0x00, //format 0
        0x00, 0x01, // one track
        0x00, 0x60, // 96per quarter - note

        0x4D, 0x54, 0x72, 0x6B, // MTrk
        0x00, 0x00, 0x00, 0x3B, // chunk length(59)

        0x00, 0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08, // time signature
        0x00, 0xFF, 0x51, 0x03, 0x07, 0xA1, 0x20, // tempo

        0x00,
        0xC0, 0x05,
        0x00,
        0xC1, 0x2E,
        0x00,
        0xC2, 0x46,
        0x00,
        0x92, 0x30, 0x60,
        0x00,
        0x3C, 0x60, // running status
        0x60,
        0x91, 0x43, 0x40,
        0x60,
        0x90, 0x4C, 0x20,
        0x81, 0x40,
        0x82, 0x30, 0x40, // two - byte delta -time
        0x00,
        0x3C, 0x40, // running status
        0x00,
        0x81, 0x43, 0x40,
        0x00,
        0x80, 0x4C, 0x40,

        0x00,
        0xFF, 0x2F, 0x00 // end of track
        };

writeIntegersAsBytes(data, fos);
        fos.close();

    }


    public static void main(String args[]) throws IOException {
        MidiFileGenerator midiFileGenerator = new MidiFileGenerator();
        FileOutputStream fos = new FileOutputStream(new File("/home/student/testMidi.mid"));
        midiFileGenerator.createMidiData(fos);
        fos.close();

//        com.kjipo.midi.MidiFileGenerator.test1();


        FileInputStream fs = new FileInputStream(new File("/home/student/testMidi.mid"));
//        FileInputStream fs = new FileInputStream(new File("/home/student/test2.mid"));
        int i = 0;
        int counter = 0;
        while((i = fs.read()) != -1) {
            System.out.println(++counter +": " +i);
        }


    }







}
