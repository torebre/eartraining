package com.kjipo.font;

import java.util.List;

public class GlyphData implements PathInterface {
    private final String name;
    private final List<PathElement> pathElements;
    private final int strokeWidth;


    public GlyphData(String name, List<PathElement> pathElements) {
        this(name, pathElements, 1);
    }

    public GlyphData(String name, List<PathElement> pathElements, int strokeWidth) {
        this.name = name;
        this.pathElements = pathElements;
        this.strokeWidth = strokeWidth;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<PathElement> getPathElements() {
        return pathElements;
    }

    @Override
    public int getStrokeWidth() {
        return strokeWidth;
    }
}
