package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;

import static org.lwjgl.glfw.GLFW.*;

// Implementation in part from: https://github.com/JOML-CI/joml-camera/blob/master/src/org/joml/camera/OrthoCameraControl.java
public class Camera {
    private Matrix3x2f viewMatrix;
    private Matrix4f viewProjectionMatrix;
    private Vector4i viewportSize;
    private float mouseX, mouseY;
    private boolean[] mouseDown = new boolean[3];
    private Vector2f normalizedDeviceCoordinates;
    private float minimalZoom;
    private float maximalZoom;

    public Camera(float extents, float minZoom, float maxZoom) {
        viewMatrix = new Matrix3x2f();
        viewProjectionMatrix = new Matrix4f();
        viewportSize = new Vector4i();
        maximalZoom = maxZoom;
        minimalZoom = minZoom;

        reload(extents);
        setupCallbacks();
    }

    public void reload(float extents) {
        viewMatrix = new Matrix3x2f();
        viewProjectionMatrix = new Matrix4f();
        normalizedDeviceCoordinates = new Vector2f();
        viewMatrix.view(-extents, extents, -extents, extents);
        update();
    }

    public void setSize(int width, int height) {
        viewportSize = new Vector4i(0, 0, width, height);
        update();
    }

    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    private void update() {
        float aspect = (float)viewportSize.z / viewportSize.w;
        viewProjectionMatrix.setOrtho2D(-aspect, +aspect, -1, +1).mul(viewMatrix);
        viewProjectionMatrix.m11(-viewProjectionMatrix.m11());
    }

    public void centerOnPosition(float x, float y) {
        viewMatrix.setTranslation(0, 0).translate(-x, -y);
        update();
    }

    public void onMouseDown(int button) {
        mouseDown[button] = true;
    }

    public void onMouseUp(int button) {
        mouseDown[button] = false;
    }

    public void onMouseMove(int winX, int winY) {
        Vector2f ndc;
        if (mouseDown[GLFW_MOUSE_BUTTON_LEFT]) {
            ndc = getNormalizedDeviceCoordinates(winX, winY);
            float x0 = ndc.x;
            float y0 = ndc.y;
            ndc = getNormalizedDeviceCoordinates(mouseX, mouseY);
            float x1 = ndc.x;
            float y1 = ndc.y;
            viewMatrix.translateLocal(x0 - x1, y0 - y1);
            update();
        }
        mouseX = winX;
        mouseY = winY;
    }

    private Vector2f getNormalizedDeviceCoordinates(float winX, float winY) {
        float aspect = (float)viewportSize.z / viewportSize.w;
        float x = (winX / viewportSize.z * 2.0f - 1.0f) * aspect;
        float y = winY / viewportSize.w * 2.0f - 1.0f;
        return normalizedDeviceCoordinates.set(x, y);
    }

    public void zoom(float scale) {
        Vector3f currentScale = new Vector3f();
        viewProjectionMatrix.getScale(currentScale);

        if ((currentScale.y > maximalZoom && scale == 1.1f) || (currentScale.y < minimalZoom && scale == 1.0f / 1.1f)) {
            return;
        }

        Vector2f ndc = getNormalizedDeviceCoordinates(mouseX, mouseY);
        viewMatrix.translateLocal(-ndc.x, -ndc.y).scaleLocal(scale, scale).translateLocal(ndc.x, ndc.y);
        update();
    }

    public Vector2f getPosition() {
        Vector3f position = new Vector3f(1.0f, 1.0f, 1.0f);
        viewProjectionMatrix.transformPosition(position);
        return new Vector2f(position.x, position.y);
    }

    public float getRotation() {
        AxisAngle4f rotation = new AxisAngle4f();
        viewProjectionMatrix.getRotation(rotation);
        return rotation.x;
    }

    public float[][] getViewAsArray() {
        return new float[][] {
                new float[] { viewMatrix.m00(), viewMatrix.m01() },
                new float[] { viewMatrix.m10(), viewMatrix.m11() },
                new float[] { viewMatrix.m20(), viewMatrix.m21() },
        };
    }

    public float[][] getViewProjectionAsArray() {
        return new float[][] {
                new float[] { viewProjectionMatrix.m00(), viewProjectionMatrix.m01(), viewProjectionMatrix.m02(), viewProjectionMatrix.m03() },
                new float[] { viewProjectionMatrix.m10(), viewProjectionMatrix.m11(), viewProjectionMatrix.m12(), viewProjectionMatrix.m13() },
                new float[] { viewProjectionMatrix.m20(), viewProjectionMatrix.m21(), viewProjectionMatrix.m22(), viewProjectionMatrix.m23() },
                new float[] { viewProjectionMatrix.m30(), viewProjectionMatrix.m31(), viewProjectionMatrix.m32(), viewProjectionMatrix.m33() }
        };
    }

    public float getZoom() {
        Vector3f currentScale = new Vector3f();
        viewProjectionMatrix.getScale(currentScale);
        return currentScale.y;
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

    private void setupCallbacks() {
        long windowHandle = Game.get().getWindow().getHandle();

        glfwSetMouseButtonCallback(windowHandle, new GLFWMouseButtonCallback() {
            public void invoke(long window, int button, int action, int mods) {
                if (Game.get().isImGuiActive()) {
                    final ImGuiIO io = ImGui.getIO();

                    if (io.getWantCaptureMouse()) {
                        return;
                    }
                }

                if (action == GLFW_PRESS) {
                    onMouseDown(button);
                } else {
                    onMouseUp(button);
                }
            }
        });

        glfwSetCursorPosCallback(windowHandle, new GLFWCursorPosCallback() {
            public void invoke(long window, double xpos, double ypos) {
                onMouseMove((int) xpos, 720 - (int) ypos);
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            public void invoke(long window, double xoffset, double yoffset) {
                if (Game.get().isImGuiActive()) {
                    final ImGuiIO io = ImGui.getIO();

                    if (io.getWantCaptureMouse()) {
                        return;
                    }
                }

                if (yoffset > 0) {
                    zoom(1.1f);
                } else {
                    zoom(1.0f / 1.1f);
                }
            }
        });
    }
}
