package com.ustudents.engine.graphic;

import org.joml.Vector2i;
import org.lwjgl.opengl.GL33;

public class Viewport {
    public static void resize(Vector2i size) {
        GL33.glViewport(0, 0, size.x, size.y);
    }
}
