package com.kjipo.representation;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by student on 7/31/14.
 */
public class Clef {
    private final ClefType clefType;
    private final List<Element> elements = new LinkedList<Element>();


    public Clef(ClefType pClefType) {
        clefType = pClefType;
    }

    public Clef addElement(Element pElement) {
        elements.add(pElement);
        return this;
    }



}
