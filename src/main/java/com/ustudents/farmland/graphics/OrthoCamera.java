package com.ustudents.farmland.graphics;

import org.joml.Matrix3x2f;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class OrthoCamera {

    private Matrix3x2f view = new Matrix3x2f();
    private Matrix4f viewproj = new Matrix4f();
    private Matrix4f invviewproj = new Matrix4f();
    private int[] vp = new int[4];
    private boolean[] mouseDown = new boolean[3];
    private Vector2f v = new Vector2f();
    private Vector3f v3 = new Vector3f();

    public OrthoCamera(float extents) {
        view.view(-extents, extents, -extents, extents);
        update();
    }

    public void setSize(int width, int height) {
        vp[0] = 0;
        vp[1] = 0;
        vp[2] = width;
        vp[3] = height;
        update();
    }

    public Matrix4f viewproj() {
        return viewproj;
    }
    private void update() {
        float aspect = (float) vp[2] / vp[3];
        viewproj.setOrtho2D(-aspect, +aspect, -1, +1)
                .mul(view)
                .invertAffine(invviewproj);
        viewproj.m11(-viewproj.m11());
    }
}
