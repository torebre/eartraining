package com.kjipo.font;

import java.util.List;

public class GlyphData implements PathInterface {
    private final String name;
    private final List<PathElement> pathElements;
    private final int strokeWidth;
    private final BoundingBox boundingBox;


    public GlyphData(String name, List<PathElement> pathElements, BoundingBox boundingBox) {
        this(name, pathElements, 1, boundingBox);
    }

    public GlyphData(String name, List<PathElement> pathElements, int strokeWidth, BoundingBox boundingBox) {
        this.name = name;
        this.pathElements = pathElements;
        this.strokeWidth = strokeWidth;
        this.boundingBox = boundingBox;
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

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return "GlyphData{" +
                "name='" + name + '\'' +
                ", pathElements=" + pathElements +
                ", strokeWidth=" + strokeWidth +
                ", boundingBox=" + boundingBox +
                '}';
    }
}
