package com.kjipo.representation;

/**
 * Created by student on 7/31/14.
 */
public class Element {
    private final ElementType elementType;
    private final int pitch;


    public Element(ElementType pElementType, int pPitch) {
        elementType = pElementType;
        pitch = pPitch;
    }


    public static Element createElement(ElementType pElementType, int pPitch) {
        return new Element(pElementType, pPitch);
    }



}
