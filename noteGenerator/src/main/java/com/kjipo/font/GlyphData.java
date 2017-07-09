package com.kjipo.font;

import java.util.List;

public class GlyphData implements PathInterface {
    private final String name;
    private final List<PathElement> pathElements;


    public GlyphData(String name, List<PathElement> pathElements) {
        this.name = name;
        this.pathElements = pathElements;
    }

    public String getName() {
        return name;
    }

    @Override
    public List<PathElement> getPathElements() {
        return pathElements;
    }
}
