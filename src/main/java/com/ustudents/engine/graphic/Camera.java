package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.event.EventListener;
import com.ustudents.engine.input.Input;
import com.ustudents.farmland.Farmland;
import imgui.ImGui;
import imgui.ImGuiIO;
import org.joml.*;
import org.joml.Math;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

// Implementation in part from: https://github.com/JOML-CI/joml-camera/blob/master/src/org/joml/camera/OrthoCameraControl.java
public class Camera {
    public enum Type {
        World,
        UI,
        Cursor
    }

    private Matrix3x2f viewMatrix;
    private Matrix4f viewProjectionMatrix;
    private Matrix4f invertViewProjectionMatrix = new Matrix4f();
    private Vector4i viewportSize;
    private float mouseX, mouseY;
    private boolean[] mouseDown = new boolean[3];
    private Vector2f normalizedDeviceCoordinates;
    private float minimalZoom;
    private float maximalZoom;
    private Type type;
    private boolean inputEnabled;

    public Camera(float extents, float minZoom, float maxZoom, Type type) {
        viewMatrix = new Matrix3x2f();
        viewProjectionMatrix = new Matrix4f();
        viewportSize = new Vector4i();
        maximalZoom = maxZoom;
        minimalZoom = minZoom;
        inputEnabled = true;
        this.type = type;

        reload(extents);

        if (inputEnabled && type == Type.World) {
            setupCallbacks();
        }
    }

    public void reload(float extents) {
        viewMatrix = new Matrix3x2f();
        viewProjectionMatrix = new Matrix4f();
        normalizedDeviceCoordinates = new Vector2f();

        if (type == Type.World) {
            viewMatrix.identity().view(-extents, extents, -extents, extents);
        }

        update();
    }

    public void resize(int width, int height) {
        viewportSize = new Vector4i(0, 0, width, height);

        update();
    }

    private void update() {
        float aspect = (float)viewportSize.z / viewportSize.w;

        if (type == Type.World) {
            viewProjectionMatrix.setOrtho2D(-aspect, aspect, -1, 1).mul(viewMatrix);
        } else {
            Vector2i size = Farmland.get().getWindow().getSize();
            viewProjectionMatrix.identity().setOrtho2D(0, size.x, -size.y, 0);
        }

        viewProjectionMatrix.m11(-viewProjectionMatrix.m11()).invertAffine(invertViewProjectionMatrix);
    }

    public void centerOnPosition(float x, float y) {
        viewMatrix.setTranslation(0, 0).translate(-x, -y);
        update();
    }

    public void zoom(float scale) {
        Vector3f currentScale = new Vector3f();
        viewProjectionMatrix.getScale(currentScale);

        if ((currentScale.y > maximalZoom && scale == 1.1f) || (currentScale.y < minimalZoom && scale == 1.0f / 1.1f)) {
            return;
        }

        Vector2f ndc = getNormalizedDeviceCoordinates(new Vector2f(mouseX, mouseY));
        viewMatrix.translateLocal(-ndc.x, -ndc.y).scaleLocal(scale, scale).translateLocal(ndc.x, ndc.y);
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
            ndc = getNormalizedDeviceCoordinates(new Vector2f(winX, winY));
            float x0 = ndc.x;
            float y0 = ndc.y;
            ndc = getNormalizedDeviceCoordinates(new Vector2f(mouseX, mouseY));
            float x1 = ndc.x;
            float y1 = ndc.y;
            viewMatrix.translateLocal(x0 - x1, y0 - y1);
            update();
        }
        mouseX = winX;
        mouseY = winY;
    }

    private Vector2f getNormalizedDeviceCoordinates(Vector2f value) {
        float aspect = (float)viewportSize.z / viewportSize.w;
        float x = (value.x / viewportSize.z * 2.0f - 1.0f) * aspect;
        float y = value.y / viewportSize.w * 2.0f - 1.0f;
        return normalizedDeviceCoordinates.set(x, y);
    }

    public Vector2f getPosition() {
        Vector2i windowSize = Farmland.get().getWindow().getSize();
        return screenCoordToWorldCoord(new Vector2f((float)windowSize.x / 2, (float)windowSize.y / 2));
    }

    public float getRotation() {
        AxisAngle4f rotation = new AxisAngle4f();
        viewProjectionMatrix.getRotation(rotation);
        return rotation.z;
    }

    public float[][] getViewAsArray() {
        return new float[][] {
                new float[] { viewMatrix.m00(), viewMatrix.m01() },
                new float[] { viewMatrix.m10(), viewMatrix.m11() },
                new float[] { viewMatrix.m20(), viewMatrix.m21() },
        };
    }

    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    public float[][] getViewProjectionAsArray() {
        return new float[][] {
                new float[] { viewProjectionMatrix.m00(), viewProjectionMatrix.m01(), viewProjectionMatrix.m02(), viewProjectionMatrix.m03() },
                new float[] { viewProjectionMatrix.m10(), viewProjectionMatrix.m11(), viewProjectionMatrix.m12(), viewProjectionMatrix.m13() },
                new float[] { viewProjectionMatrix.m20(), viewProjectionMatrix.m21(), viewProjectionMatrix.m22(), viewProjectionMatrix.m23() },
                new float[] { viewProjectionMatrix.m30(), viewProjectionMatrix.m31(), viewProjectionMatrix.m32(), viewProjectionMatrix.m33() }
        };
    }

    public Matrix4f getInvertViewProjectionMatrix() {
        return invertViewProjectionMatrix;
    }

    public Vector4f getViewFrustum() {
        Vector3f vector = new Vector3f();
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;

        for (int i = 0; i < 4; i++) {
            float x = ((i & 1) << 1) - 1.0f;
            float y = (((i >>> 1) & 1) << 1) - 1.0f;

            invertViewProjectionMatrix.transformPosition(vector.set(x, y, 0));

            minX = java.lang.Math.min(minX, vector.x);
            minY = java.lang.Math.min(minY, vector.y);
            maxX = java.lang.Math.max(maxX, vector.x);
            maxY = java.lang.Math.max(maxY, vector.y);
        }

        return new Vector4f(minX, minY, maxX, maxY);
    }

    public Vector2f screenCoordToWorldCoord(Vector2f screenCoord) {
        Vector3f value = new Vector3f();
        Vector2i windowSize = Farmland.get().getWindow().getSize();
        viewProjectionMatrix.unproject(new Vector3f(screenCoord.x, windowSize.y - screenCoord.y, 0),
                new int[] {0, 0, windowSize.x, windowSize.y}, value);
        return new Vector2f(value.x, value.y);
    }

    public Vector2f worldCoordToScreenCoord(Vector2f worldCoord) {
        Vector3f value = new Vector3f();
        Vector2i windowSize = Farmland.get().getWindow().getSize();
        viewProjectionMatrix.project(new Vector3f(worldCoord.x, worldCoord.y, 0),
                new int[] {0, 0, windowSize.x, windowSize.y}, value);
        return new Vector2f((float)windowSize.x / 2 + value.x, value.y - (float)windowSize.y / 2);
    }

    public float getZoom() {
        Vector3f currentScale = new Vector3f();
        viewProjectionMatrix.getScale(currentScale);
        return currentScale.y;
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
                Farmland.get().getImGuiManager().getImGuiGlfw().mouseButtonCallback(window, button, action, mods);

                if (inputEnabled) {
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
            }
        });

        Input.mouseMoved.add(() -> {
            if (inputEnabled) {
                Vector2f mousePos = Input.getMousePos();
                onMouseMove((int) mousePos.x, Farmland.get().getWindow().getSize().y - (int) mousePos.y);
            }
        });

        glfwSetScrollCallback(windowHandle, new GLFWScrollCallback() {
            public void invoke(long window, double xoffset, double yoffset) {
                Farmland.get().getImGuiManager().getImGuiGlfw().scrollCallback(window, xoffset, yoffset);

                if (inputEnabled) {
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
            }
        });
    }

    public void enableInput(boolean enabled) {
        inputEnabled = enabled;
    }
}
