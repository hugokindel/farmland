package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.In;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.core.window.events.CursorMovedEvent;
import com.ustudents.engine.core.window.events.MouseButtonStateChangedEvent;
import com.ustudents.engine.core.window.events.ScrollMovedEvent;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.input.Key;
import com.ustudents.engine.input.MouseButton;
import org.joml.*;

import static org.lwjgl.glfw.GLFW.*;

// Implementation in part from: https://github.com/JOML-CI/joml-camera/blob/master/src/org/joml/camera/OrthoCameraControl.java
@SuppressWarnings("unchecked")
public class Camera {
    public static class PositionChanged extends Event {
        public Vector2f position;

        public PositionChanged(Vector2f position) {
            this.position = position;
        }
    }

    public static class MousePositionChanged extends Event {
        public Vector2f oldMousePosition;
        public Vector2f newMousePosition;

        public MousePositionChanged(Vector2f oldMousePosition, Vector2f newMousePosition) {
            this.oldMousePosition = oldMousePosition;
            this.newMousePosition = newMousePosition;
        }
    }

    public enum Type {
        World,
        UI,
        Cursor
    }

    private Matrix3x2f viewMatrix;
    private Matrix4f viewProjectionMatrix;
    private Matrix4f invertViewProjectionMatrix = new Matrix4f();
    private Vector4i viewportSize;
    private Vector2f mousePosition;
    private boolean[] mouseDown = new boolean[8];
    private Vector2f normalizedDeviceCoordinates;
    private float minimalZoom;
    private float maximalZoom;
    private float minimalX;
    private float maximalX;
    private float minimalY;
    private float maximalY;
    private boolean hasMinimalX;
    private boolean hasMinimalY;
    private boolean hasMaximalX;
    private boolean hasMaximalY;
    private float zoom;
    private Type type;
    private boolean inputEnabled;
    private Vector2f position;
    private float rotation;
    float[][] viewProjectionArray;
    float[][] viewArray;
    private Vector4f viewFrustum;

    public EventDispatcher moved = new EventDispatcher(PositionChanged.class);

    public EventDispatcher mouseMoved = new EventDispatcher(MousePositionChanged.class);

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
            Vector2i size = Game.get().getWindow().getSize();
            viewProjectionMatrix.identity().setOrtho2D(0, size.x, -size.y, 0);
        }

        viewProjectionMatrix.m11(-viewProjectionMatrix.m11()).invertAffine(invertViewProjectionMatrix);

        Vector3f currentScale = new Vector3f();
        viewProjectionMatrix.getScale(currentScale);
        zoom = currentScale.y;

        Vector2i windowSize = Game.get().getWindow().getSize();
        position = screenCoordToWorldCoord(new Vector2f((float)windowSize.x / 2, (float)windowSize.y / 2));

        rotation = viewProjectionMatrix.getRotation( new AxisAngle4f()).z;

        viewProjectionArray = new float[][] {
                new float[] { viewProjectionMatrix.m00(), viewProjectionMatrix.m01(), viewProjectionMatrix.m02(), viewProjectionMatrix.m03() },
                new float[] { viewProjectionMatrix.m10(), viewProjectionMatrix.m11(), viewProjectionMatrix.m12(), viewProjectionMatrix.m13() },
                new float[] { viewProjectionMatrix.m20(), viewProjectionMatrix.m21(), viewProjectionMatrix.m22(), viewProjectionMatrix.m23() },
                new float[] { viewProjectionMatrix.m30(), viewProjectionMatrix.m31(), viewProjectionMatrix.m32(), viewProjectionMatrix.m33() }
        };

        viewArray = new float[][] {
                new float[] { viewMatrix.m00(), viewMatrix.m01() },
                new float[] { viewMatrix.m10(), viewMatrix.m11() },
                new float[] { viewMatrix.m20(), viewMatrix.m21() },
        };

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

        viewFrustum = new Vector4f(minX, minY, maxX, maxY);

        moved.dispatch(new PositionChanged(getPosition()));
    }

    public void centerOnPosition(Vector2f position) {
        viewMatrix.setTranslation(0, 0).translate(-position.x, position.y);
        update();
    }

    public void zoom(float scale) {
        if (mousePosition != null) {
            Vector3f currentScale = new Vector3f();
            viewProjectionMatrix.getScale(currentScale);

            if ((currentScale.y > maximalZoom && scale == 1.1f) || (currentScale.y < minimalZoom && scale == 1.0f / 1.1f)) {
                return;
            }

            Vector2f ndc = getNormalizedDeviceCoordinates(mousePosition);
            viewMatrix.translateLocal(-ndc.x, -ndc.y).scaleLocal(scale, scale).translateLocal(ndc.x, ndc.y);

            update();
        }
    }

    public void onMouseDown(int button) {
        mouseDown[button] = true;
    }

    public void onMouseUp(int button) {
        mouseDown[button] = false;
    }

    public void moveToMousePosition(Vector2f newMousePosition) {
        mouseMoved.dispatch(new MousePositionChanged(newMousePosition, mousePosition));
        mousePosition = newMousePosition;
    }

    public void moveLeft(float newPos) {
        moveTo(new Vector2f(position.x + newPos, position.y), position, 0);
    }

    public void moveRight(float newPos) {
        moveTo(new Vector2f(position.x - newPos, position.y), position, 0);
    }

    public void moveTop(float newPos) {
        moveTo(new Vector2f(position.x, position.y - newPos), position, 0);
    }

    public void moveBottom(float newPos) {
        moveTo(new Vector2f(position.x, position.y + newPos), position, 0);
    }

    public void moveTo(Vector2f newPosition, Vector2f oldPosition, int type) {
        if (type == 0) {
            if ((hasMaximalX && position.x > maximalX  && position.x > newPosition.x) ||
                    (hasMaximalY && position.y > maximalY && position.y < newPosition.y) ||
                    (hasMinimalX && position.x < minimalX && position.x < newPosition.x) ||
                    (hasMinimalY && position.y < minimalY && position.y > newPosition.y)) {
                return;
            }
        } else {
            if ((hasMaximalX && position.x > maximalX  && oldPosition.x > newPosition.x) ||
                    (hasMaximalY && position.y > maximalY && oldPosition.y < newPosition.y) ||
                    (hasMinimalX && position.x < minimalX && oldPosition.x < newPosition.x) ||
                    (hasMinimalY && position.y < minimalY && oldPosition.y > newPosition.y)) {
                return;
            }
        }

        Vector2f ndc = getNormalizedDeviceCoordinates(newPosition);
        float x0 = ndc.x;
        float y0 = ndc.y;
        ndc = getNormalizedDeviceCoordinates(oldPosition);
        float x1 = ndc.x;
        float y1 = ndc.y;
        viewMatrix.translateLocal(x0 - x1, y0 - y1);
        update();
    }

    private Vector2f getNormalizedDeviceCoordinates(Vector2f value) {
        float aspect = (float)viewportSize.z / viewportSize.w;
        float x = (value.x / viewportSize.z * 2.0f - 1.0f) * aspect;
        float y = value.y / viewportSize.w * 2.0f - 1.0f;
        return normalizedDeviceCoordinates.set(x, y);
    }

    public Vector2f getPosition() {
        return position;
    }

    public float getRotation() {
        return rotation;
    }

    public float[][] getViewAsArray() {
        return viewArray;
    }

    public Matrix4f getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }

    public float[][] getViewProjectionAsArray() {
        return viewProjectionArray;
    }

    public Matrix4f getInvertViewProjectionMatrix() {
        return invertViewProjectionMatrix;
    }

    public Vector4f getViewFrustum() {
        return viewFrustum;
    }

    public Vector2f screenCoordToWorldCoord(Vector2f screenCoord) {
        Vector3f value = new Vector3f();
        Vector2i windowSize = Game.get().getWindow().getSize();
        viewProjectionMatrix.unproject(new Vector3f(screenCoord.x, windowSize.y - screenCoord.y, 0),
                new int[] {0, 0, windowSize.x, windowSize.y}, value);
        return new Vector2f(value.x, value.y);
    }

    public Vector2f worldCoordToScreenCoord(Vector2f worldCoord) {
        if (type == Type.UI) {
            return worldCoord;
        } else {
            Vector3f value = new Vector3f();
            Vector2i windowSize = Game.get().getWindow().getSize();
            viewProjectionMatrix.project(new Vector3f(worldCoord.x, worldCoord.y, 0),
                    new int[] {0, 0, windowSize.x, windowSize.y}, value);
            return new Vector2f((float)windowSize.x / 2 + value.x, value.y - (float)windowSize.y / 2);
        }
    }

    public float getZoom() {
        return zoom;
    }

    public float getMinimalZoom() {
        return minimalZoom;
    }

    public float getMaximalZoom() {
        return maximalZoom;
    }

    private void setupCallbacks() {
        long windowHandle = Game.get().getWindow().getHandle();

        Window.get().getMouseButtonStateChanged().add((dataType, data) -> {
            if (inputEnabled) {
                MouseButtonStateChangedEvent eventData = (MouseButtonStateChangedEvent) data;

                if (eventData.action == GLFW_PRESS) {
                    onMouseDown(eventData.button);
                } else {
                    onMouseUp(eventData.button);
                }
            }
        });

        Window.get().getCursorMoved().add((dataType, data) -> {
            if (inputEnabled) {
                CursorMovedEvent eventData = (CursorMovedEvent) data;
                moveToMousePosition(new Vector2f((int) eventData.position.x, Game.get().getWindow().getSize().y - (int) eventData.position.y));
            }
        });

        Window.get().getScrollMoved().add((dataType, data) -> {
            if (inputEnabled) {
                ScrollMovedEvent eventData = (ScrollMovedEvent) data;

                if (/*Input.isKeyDown(Key.LeftAlt) || Input.isKeyDown(Key.RightAlt)*/true) {
                    if (eventData.offets.y > 0) {
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

    public void setMinimalX(float minimalX) {
        this.minimalX = minimalX;
        this.hasMinimalX = true;
    }

    public void setMaximalX(float maximalX) {
        this.maximalX = maximalX;
        this.hasMaximalX = true;
    }

    public void setMinimalY(float minimalY) {
        this.minimalY = minimalY;
        this.hasMinimalY = true;
    }

    public void setMaximalY(float maximalY) {
        this.maximalY = maximalY;
        this.hasMaximalY = true;
    }

    public void setMinimalZoom(float minimalZoom) {
        this.minimalZoom = minimalZoom;
    }

    public void setMaximalZoom(float maximalZoom) {
        this.maximalZoom = maximalZoom;
    }
}
