package com.ustudents.engine.graphic;

import org.joml.*;
import org.joml.Math;

public class Camera {

    public static int MOUSE_LEFT = 0;
    public static int MOUSE_RIGHT = 1;
    public static int MOUSE_CENTER = 2;

    private Matrix3x2f view = new Matrix3x2f();
    private Matrix4f viewproj = new Matrix4f();
    private Matrix4f invviewproj = new Matrix4f();
    private int[] vp = new int[4];
    private float mouseX, mouseY;
    private float mouseDownX, mouseDownY;
    private boolean[] mouseDown = new boolean[3];
    private Vector2f v = new Vector2f();
    private Vector3f v3 = new Vector3f();
    private float zoom;
    private float minimalZoom;
    private float maximalZoom;

    private float minRotateWinDistance2 = 100.0f * 100.0f;

    public Camera(float extents, float minZoom, float maxZoom) {
        maximalZoom = maxZoom;
        minimalZoom = minZoom;
        reload(extents);
    }

    public void reload(float extents) {
        if (extents >= maximalZoom) {
            extents = maximalZoom;
        } else if (extents <= minimalZoom) {
            extents = minimalZoom;
        }
        view = new Matrix3x2f();
        viewproj = new Matrix4f();
        invviewproj = new Matrix4f();
        zoom = extents;
        view.view(-extents, extents, -extents, extents);
        update();
    }

    public void setMinRotateWinDistance(float minRotateWinDistance) {
        this.minRotateWinDistance2 = minRotateWinDistance * minRotateWinDistance;
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
    public Matrix4f invviewproj() {
        return invviewproj;
    }

    private void update() {
        float aspect = (float) vp[2] / vp[3];
        viewproj.setOrtho2D(-aspect, +aspect, -1, +1)
                .mul(view)
                .invertAffine(invviewproj);
        viewproj.m11(-viewproj.m11());
    }

    public void center(float x, float y) {
        view.setTranslation(0, 0).translate(-x, -y);
        update();
    }

    public void onMouseDown(int button) {
        mouseDownX = mouseX;
        mouseDownY = mouseY;
        mouseDown[button] = true;
        if (button == MOUSE_CENTER) {
            /* Reset rotation with mouse position as center */
            view.positiveX(v);
            float ang = (float) Math.atan2(v.y, v.x);
            Vector2f ndc = ndc(mouseDownX, mouseDownY);
            view.translateLocal(-ndc.x, -ndc.y)
                    .rotateLocal(ang)
                    .translateLocal(ndc.x, ndc.y);
            update();
        }
    }

    public void onMouseUp(int button) {
        mouseDown[button] = false;
    }

    public void onMouseMove(int winX, int winY) {
        Vector2f ndc;
        if (mouseDown[MOUSE_LEFT]) {
            /* Move */
            ndc = ndc(winX, winY);
            float x0 = ndc.x, y0 = ndc.y;
            ndc = ndc(mouseX, mouseY);
            float x1 = ndc.x, y1 = ndc.y;
            view.translateLocal(x0 - x1, y0 - y1);
            update();
        } else if (mouseDown[MOUSE_RIGHT]) {
            /* Check if rotation is possible */
            float dx = winX - mouseDownX;
            float dy = winY - mouseDownY;
            if (dx * dx + dy * dy > minRotateWinDistance2) {
                /* Rotate */
                float dx0 = winX - mouseDownX, dy0 = winY - mouseDownY;
                float dx1 = mouseX - mouseDownX, dy1 = mouseY - mouseDownY;
                float ang = (float) Math.atan2(dx1 * dy0 - dy1 * dx0, dx1 * dx0 + dy1 * dy0);
                ndc = ndc(mouseDownX, mouseDownY);
                view.translateLocal(-ndc.x, -ndc.y)
                        .rotateLocal(ang)
                        .translateLocal(ndc.x, ndc.y);
                update();
            }
        }
        mouseX = winX;
        mouseY = winY;
    }

    private Vector2f ndc(float winX, float winY) {
        float aspect = (float) vp[2] / vp[3];
        float x = (winX / vp[2] * 2.0f - 1.0f) * aspect;
        float y = winY / vp[3] * 2.0f - 1.0f;
        return v.set(x, y);
    }

    public void zoom(float scale) {
        Vector2f ndc = ndc(mouseX, mouseY);
        view.translateLocal(-ndc.x, -ndc.y)
                .scaleLocal(scale, scale)
                .translateLocal(ndc.x, ndc.y);
        update();
    }

    public Vector4f viewRect(Vector4f dest) {
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY;
        for (int i = 0; i < 4; i++) {
            float x = ((i & 1) << 1) - 1.0f;
            float y = (((i >>> 1) & 1) << 1) - 1.0f;
            invviewproj.transformPosition(v3.set(x, y, 0));
            minX = java.lang.Math.min(minX, v3.x);
            minY = java.lang.Math.min(minY, v3.y);
            maxX = java.lang.Math.max(maxX, v3.x);
            maxY = java.lang.Math.max(maxY, v3.y);
        }
        return dest.set(minX, minY, maxX, maxY);
    }

    public void viewSpan(Vector2f cornerDest, Vector2f xDest, Vector2f yDest) {
        invviewproj.transformPosition(v3.set(-1, -1, 0));
        cornerDest.set(v3.x, v3.y);
        xDest.set(2*invviewproj.m00(), 2*invviewproj.m10());
        yDest.set(2*invviewproj.m01(), 2*invviewproj.m11());
    }

    public Vector2f getPosition() {
        Vector3f position = new Vector3f(1.0f, 1.0f, 1.0f);
        viewproj.transformPosition(position);
        return new Vector2f(position.x, position.y);
    }

    public float getRotation() {
        AxisAngle4f rotation = new AxisAngle4f();
        viewproj.getRotation(rotation);
        return rotation.x;
    }

    public float[][] getViewAsArray() {
        return new float[][] {
                new float[] { view.m00(), view.m01() },
                new float[] { view.m10(), view.m11() },
                new float[] { view.m20(), view.m21() },
        };
    }

    public float[][] getViewProjectionAsArray() {
        return new float[][] {
                new float[] { viewproj.m00(), viewproj.m01(), viewproj.m02(), viewproj.m03() },
                new float[] { viewproj.m10(), viewproj.m11(), viewproj.m12(), viewproj.m13() },
                new float[] { viewproj.m20(), viewproj.m21(), viewproj.m22(), viewproj.m23() },
                new float[] { viewproj.m30(), viewproj.m31(), viewproj.m32(), viewproj.m33() }
        };
    }

    public float getZoom(){
        return zoom;
    }

    public void setZoom(float value){
        reload(value);
    }

    public float getMinimalZoom() {
        return minimalZoom;
    }

    public float getMaximalZoom() {
        return maximalZoom;
    }
}
