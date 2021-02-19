package com.ustudents.engine.graphic;

import static org.lwjgl.opengl.GL33.*;

public class VertexVariable {
    public final String name;

    public final int type;

    public final int typeSize;

    public final int size;

    public final int location;

    public VertexVariable(String name, int type, int size, int location) {
        this.name = name;
        this.location = location;

        if (type == GL_FLOAT_VEC4) {
            this.type = GL_FLOAT;
            this.size = 4;
            this.typeSize = 4;
        } else if (type == GL_FLOAT_VEC3) {
            this.type = GL_FLOAT;
            this.size = 3;
            this.typeSize = 4;
        } else if (type == GL_FLOAT_VEC2) {
            this.type = GL_FLOAT;
            this.size = 2;
            this.typeSize = 4;
        } else if (type == GL_FLOAT) {
            this.type = GL_FLOAT;
            this.size = 1;
            this.typeSize = 4;
        } else if (type == GL_FLOAT_MAT4) {
            this.type = GL_FLOAT;
            this.size = 1;
            this.typeSize = 4;
        } else if (type == GL_SAMPLER_2D) {
            this.type = GL_SAMPLER_2D;
            this.size = 1;
            this.typeSize = 4;
        } else {
            this.type = type;
            this.size = size;
            this.typeSize = 4;
        }
    }

    public void bind() {

        glVertexAttribPointer(location, size, type, false, (size * 2) * typeSize,
                (long)location * ((long)size * typeSize));
        glEnableVertexAttribArray(location);
    }
}
