package com.kjipo.font;

import java.util.List;

public class PathInterfaceImpl implements PathInterface {
    private final List<PathElement> pathElements;
    private final int strokeWidth;


    public PathInterfaceImpl(List<PathElement> pathElements, int strokeWidth) {
        this.pathElements = pathElements;
        this.strokeWidth = strokeWidth;
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
