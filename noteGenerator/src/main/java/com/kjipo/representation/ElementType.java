package com.kjipo.representation;


public enum ElementType {
    QUARTERNOTE(true, 1),
    HALFNOTE(true, 2);

    public final boolean isNote;
    public final int duration;

    ElementType(boolean isNote, int duration) {
        this.isNote = isNote;
        this.duration = duration;
    }



}
