package com.kjipo.font;

import java.util.List;

public class GlyphData {
    private final String name;
    private final List<FontPathElement> fontPathElements;


    public GlyphData(String name, List<FontPathElement> fontPathElements) {
        this.name = name;
        this.fontPathElements = fontPathElements;
    }

    public String getName() {
        return name;
    }

    public List<FontPathElement> getFontPathElements() {
        return fontPathElements;
    }
}
