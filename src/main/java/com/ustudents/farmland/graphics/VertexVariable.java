package com.ustudents.farmland.graphics;

public class VertexVariable {
    public final int type;
    public final int size;
    public final int location;

    public VertexVariable(int type, int size, int location) {
        this.type = type;
        this.size = size;
        this.location = location;
    }

    @Override
    public String toString() {
        return "VertexAttribute{" +
                "type=" + type +
                ", size=" + size +
                ", location=" + location +
                '}';
    }
}
