package com.kjipo.midi;


public enum MidiMessages {
    NOTE_ON(0x90),
    NOTE_OFF(0x80);

    public int message;

    MidiMessages(int message) {
        this.message = message;
    }
}
