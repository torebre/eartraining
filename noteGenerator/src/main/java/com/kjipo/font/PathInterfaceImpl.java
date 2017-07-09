package com.kjipo.font;

import java.util.List;

public class PathInterfaceImpl implements PathInterface {
    private final List<PathElement> pathElements;


    public PathInterfaceImpl(List<PathElement> pathElements) {
        this.pathElements = pathElements;
    }


    @Override
    public List<PathElement> getPathElements() {
        return pathElements;
    }
}
