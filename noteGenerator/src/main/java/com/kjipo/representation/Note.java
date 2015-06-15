package com.kjipo.representation;


public class Note extends SequenceElement {
    private final int pitch;


    public Note(ElementType elementType, int pitch) {
        super(elementType);
        this.pitch = pitch;
    }


    public int getPitch() {
        return pitch;
    }
}
