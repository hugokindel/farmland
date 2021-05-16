package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.utility.MathUtil;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.List;

import static org.lwjgl.opengl.GL33.*;

@SuppressWarnings({"unused"})
public class Spritebatch {
    public enum ElementType {
        Sprite,
        TruetypeFont
    }

    public static class Data {
        Texture texture;

        Vector2f position;

        Vector2f dimensions;

        Vector2f origin;

        Vector2f scale;

        Vector4f region;

        Color tint;

        float rotation;

        int zIndex;

        ElementType type;

        public int getzIndex() {
            return zIndex;
        }

        public int getType() {
            switch (type) {
                case Sprite:
                    return 0;
                case TruetypeFont:
                    return 1;
                default:
                    return -1;
            }
        }
    }

    public class Renderer {
        FloatBuffer vertices;

        private final int vao;

        private final int vbo;

        private final int ebo;

        public Renderer(int maxNumberOfSprites, Set<VertexVariable> attributes) {
            vertices = BufferUtils.createFloatBuffer(maxNumberOfSprites * 32);

            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            ebo = glGenBuffers();

            shader.bind();

            // This happens within the shader context.
            {
                glBindVertexArray(vao);

                // This happens within the VAO context.
                {
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);

                    for (VertexVariable attribute : attributes) {
                        attribute.bind();
                    }

                    glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

                    // Defines 6 vertices with the following indices:
                    //
                    // 0---1
                    // |  /|
                    // | / |
                    // 2---3
                    int[] indices = new int[]{0, 1, 2, 2, 3, 1};
                    IntBuffer indicesArray = BufferUtils.createIntBuffer(maxNumberOfSprites * 6);

                    for (int i = 0; i < maxNumberOfSprites; ++i) {
                        for (int j = 0; j < 6; ++j) {
                            indicesArray.put(i * 6 + j, indices[j] + i * 4);
                        }
                    }

                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesArray, GL_DYNAMIC_DRAW);

                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }

                glBindVertexArray(0);
            }

            glUseProgram(0);
        }

        public void destroy() {
            glDeleteBuffers(vbo);
            glDeleteBuffers(ebo);
            glDeleteVertexArrays(vao);
        }

        public void clear() {
            vertices.clear();
        }

        public void putElement(Data data) {
            if (data.type == ElementType.Sprite) {
                data.position = new Vector2f(
                        data.position.x - (data.origin.x * data.scale.x),
                        data.position.y - (data.origin.y * data.scale.y));
                Vector2f position1 = new Vector2f(data.position.x, data.position.y);
                Vector2f position2 = new Vector2f(data.position.x + data.scale.x * data.dimensions.x,
                        data.position.y);
                Vector2f position3 = new Vector2f(data.position.x,
                        data.position.y  + data.scale.y * data.dimensions.y);
                Vector2f position4 = new Vector2f(data.position.x + data.scale.x * data.dimensions.x,
                        data.position.y + data.scale.y * data.dimensions.y);

                if (data.rotation != 0.0f) {
                    Vector2f rotationOrigin = new Vector2f(position1.x + (data.origin.x * data.scale.x),
                            position1.y + (data.origin.y * data.scale.y));
                    position1 = MathUtil.rotatePosition(position1, rotationOrigin, data.rotation);
                    position2 = MathUtil.rotatePosition(position2, rotationOrigin, data.rotation);
                    position3 = MathUtil.rotatePosition(position3, rotationOrigin, data.rotation);
                    position4 = MathUtil.rotatePosition(position4, rotationOrigin, data.rotation);
                }

                // Top left (0).
                putVertex(new Vector4f(
                        position1.x,
                        position1.y,
                        data.region.x,
                        data.region.y
                ), data.tint);

                // Top right (1).
                putVertex(new Vector4f(
                        position2.x,
                        position2.y,
                        data.region.z,
                        data.region.y
                ), data.tint);

                // Bottom left (2).
                putVertex(new Vector4f(
                        position3.x,
                        position3.y,
                        data.region.x,
                        data.region.w
                ), data.tint);

                // Bottom right (3).
                putVertex(new Vector4f(
                        position4.x,
                        position4.y,
                        data.region.z,
                        data.region.w
                ), data.tint);
            } else {
                // Top left (0).
                putVertex(new Vector4f(
                        data.position.x,
                        data.position.y,
                        data.region.x,
                        data.region.y
                ), data.tint);

                // Top right (1).
                putVertex(new Vector4f(
                        data.dimensions.x,
                        data.position.y,
                        data.region.z,
                        data.region.y
                ), data.tint);

                // Bottom left (2).
                putVertex(new Vector4f(
                        data.position.x,
                        data.dimensions.y,
                        data.region.x,
                        data.region.w
                ), data.tint);

                // Bottom right (3).
                putVertex(new Vector4f(
                        data.dimensions.x,
                        data.dimensions.y,
                        data.region.z,
                        data.region.w
                ), data.tint);
            }
        }

        public void putVertex(Vector4f position, Color colorTint) {
            vertices.put(position.x);
            vertices.put(position.y);
            vertices.put(position.z);
            vertices.put(position.w);
            vertices.put(colorTint.r);
            vertices.put(colorTint.g);
            vertices.put(colorTint.b);
            vertices.put(colorTint.a);
        }

        public void draw() {
            shader.bind();

            // This happens within the shader context.
            {
                shader.setUniform1f("alpha", globalAlpha);
                shader.setUniformMatrix4fv("projection", projection);
                shader.setUniform1i("type",  data.get(0).getType());

                glBindVertexArray(vao);

                // This happens within the VAO context.
                {
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

                    for (int i = 0; i < size; i++) {
                        putElement(data.get(i));
                    }

                    glBufferSubData(GL_ARRAY_BUFFER, 0, (FloatBuffer) vertices.flip());

                    Texture lastTexture = data.get(0).texture;
                    int lastType = data.get(0).getType();
                    int offset = 0;

                    for (int i = 0; i < size; i++) {
                        Data sprite = data.get(i);

                        if (sprite.texture != lastTexture) {
                            shader.setUniform1i("type", lastType);
                            glBindTexture(GL_TEXTURE_2D, lastTexture.getHandle());
                            glDrawElements(GL_TRIANGLES, (i - offset) * 6, GL_UNSIGNED_INT,
                                    (long)offset * 6 * 4);

                            offset = i;
                            lastTexture = sprite.texture;
                            lastType = sprite.getType();
                        }
                    }

                    shader.setUniform1i("type", lastType);
                    glBindTexture(GL_TEXTURE_2D, lastTexture.getHandle());
                    glDrawElements(GL_TRIANGLES, (size - offset) * 6, GL_UNSIGNED_INT,
                            (long)offset * 6 * 4);

                    glBindTexture(GL_TEXTURE_2D, 0);
                    glBindBuffer(GL_ARRAY_BUFFER, 0);
                }

                glBindVertexArray(0);
            }

            glUseProgram(0);
        }
    }

    public static class TextureData {
        public Texture texture;

        public Vector2f position;

        public Vector4f region;

        public int zIndex;

        public Color tint;

        public float rotation;

        public Vector2f scale;

        public Vector2f origin;

        public TextureData(Texture texture, Vector2f position) {
            this.texture = texture;
            this.position = position;
            this.region = new Vector4f(0.0f, 0.0f, texture.getWidth(), texture.getHeight());
            this.zIndex = 0;
            this.tint = Color.WHITE;
            this.rotation = 0.0f;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Vector2f();
        }
    }
    
    public static class SpriteData {
        public Sprite sprite;

        public Vector2f position;

        public int zIndex;

        public Color tint;

        public float rotation;

        public Vector2f scale;

        public Vector2f origin;

        public SpriteData(Sprite sprite, Vector2f position) {
            this.sprite = sprite;
            this.position = position;
            this.zIndex = 0;
            this.tint = Color.WHITE;
            this.rotation = 0.0f;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Vector2f();
        }
    }

    public static class NineSlicedSpriteData {
        public NineSlicedSprite sprite;

        public Vector2f position;

        public Vector2f size;

        public int zIndex;

        public Color tint;

        public float rotation;

        public Vector2f scale;

        public Vector2f origin;

        public NineSlicedSpriteData(NineSlicedSprite sprite, Vector2f position, Vector2f size) {
            this.sprite = sprite;
            this.position = position;
            this.size = size;
            this.zIndex = 0;
            this.tint = Color.WHITE;
            this.rotation = 0.0f;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Vector2f();
        }
    }

    public static class RectangleData {
        public Vector2f position;

        public Vector2f size;

        public int zIndex;

        public Color color;

        public float rotation;

        public Vector2f scale;

        public Vector2f origin;

        public boolean filled;

        public int thickness;

        public RectangleData(Vector2f position, Vector2f size) {
            this.position = position;
            this.size = size;
            this.zIndex = 0;
            this.color = Color.WHITE;
            this.rotation = 0.0f;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Vector2f();
            this.filled = true;
            this.thickness = 1;
        }
    }

    public static class PointData {
        public Vector2f position;

        public int zIndex;

        public Color color;

        public PointData(Vector2f position) {
            this.position = position;
            this.zIndex = 0;
            this.color = Color.WHITE;
        }
    }

    public static class PointsData {
        public Vector2f position;

        public List<Vector2f> points;

        public int zIndex;

        public Color color;

        public int thickness;

        public PointsData(Vector2f position, List<Vector2f> points) {
            this.position = position;
            this.points = points;
            this.zIndex = 0;
            this.color = Color.WHITE;
            this.thickness = 1;
        }
    }

    public static class LineData {
        public Vector2f point1;

        public Vector2f point2;

        public int zIndex;

        public Color color;

        public float thickness;

        public LineData(Vector2f point1, Vector2f point2) {
            this.point1 = point1;
            this.point2 = point2;
            this.zIndex = 0;
            this.color = Color.WHITE;
            this.thickness = 1;
        }
    }

    public static class CircleData {
        public Vector2f position;

        public float radius;

        public int sides;

        public int zIndex;

        public Color color;

        public int thickness;

        public CircleData(Vector2f position, float radius, int sides) {
            this.position = position;
            this.radius = radius;
            this.sides = sides;
            this.zIndex = 0;
            this.color = Color.WHITE;
            this.thickness = 0;
        }
    }

    public static class TextData {
        public String text;

        public Font font;

        public Vector2f position;

        public int zIndex;

        public Color color;

        public float rotation;

        public Vector2f scale;

        public Vector2f origin;

        public TextData(String text, Font font, Vector2f position) {
            this.text = text;
            this.font = font;
            this.position = position;
            this.zIndex = 0;
            this.color = Color.WHITE;
            this.rotation = 0.0f;
            this.scale = new Vector2f(1.0f, 1.0f);
            this.origin = new Vector2f();
        }
    }

    private final Shader shader;

    private final Renderer renderer;

    private Camera camera;

    private final List<Data> data;

    private int size;

    private Matrix4f projection;

    private float globalAlpha;

    private final Texture primitiveTexture;

    private boolean destroyed;

    public Spritebatch() {
        this(Game.get().getSceneManager().getCurrentScene().getWorldCamera());
    }

    public Spritebatch(Camera camera) {
        this(camera, Objects.requireNonNull(Resources.loadShader("spritebatch")));
    }

    public Spritebatch(Camera camera, Shader shader) {
        this.data = new ArrayList<>(16384);
        this.shader = shader;
        this.camera = camera;
        this.renderer = new Renderer(16384, shader.getVertexAttributes());
        this.destroyed = false;
        this.projection = new Matrix4f();
        this.primitiveTexture = new Texture(
                new byte[] {(byte)255, (byte)255, (byte)255, (byte)255}, 1, 1, 4
        );

        if (Game.isDebugging()) {
            Out.printlnDebug("Spritebatch created.");
        }
    }

    public void destroy() {
        if (!destroyed) {
            renderer.destroy();
            primitiveTexture.destroy();
            destroyed = true;

            if (Game.isDebugging()) {
                Out.printlnDebug("Spritebatch destroyed.");
            }
        }
    }

    public void begin() {
        clear();

        if (!projection.equals(camera.getViewProjectionMatrix())) {
            projection = camera.getViewProjectionMatrix().get(new Matrix4f());
        }

        if (globalAlpha != 1.0f) {
            globalAlpha = 1.0f;
        }
    }

    public void begin(Camera camera) {
        this.camera = camera;

        begin();
    }

    public void drawTexture(TextureData textureData) {
        drawTexture(
                textureData.texture,
                textureData.position,
                textureData.region,
                textureData.zIndex,
                textureData.tint,
                textureData.rotation,
                textureData.scale,
                textureData.origin);
    }

    public void drawSprite(SpriteData spriteData) {
        drawTexture(
                spriteData.sprite.getTexture(),
                spriteData.position,
                spriteData.sprite.getRegion(),
                spriteData.zIndex,
                spriteData.tint,
                spriteData.rotation,
                spriteData.scale,
                spriteData.origin);
    }

    public void drawNineSlicedSprite(NineSlicedSpriteData spriteRenderer) {
        Vector2f realPosition = new Vector2f(
                spriteRenderer.position.x - spriteRenderer.scale.x * spriteRenderer.origin.x,
                spriteRenderer.position.y - spriteRenderer.scale.y * spriteRenderer.origin.y);
        Vector2f realSize = new Vector2f(
                spriteRenderer.size.x == 0 ? 1 : spriteRenderer.size.x / spriteRenderer.scale.x,
                spriteRenderer.size.y == 0 ? 1 : spriteRenderer.size.y / spriteRenderer.scale.y);
        int numWidthNeeded = (int)(realSize.x / spriteRenderer.sprite.middle.getRegion().z);
        int numHeightNeeded = (int)(realSize.y / spriteRenderer.sprite.middle.getRegion().w);

        // Top left.
        SpriteData topLeft = new SpriteData(spriteRenderer.sprite.topLeft, realPosition);
        topLeft.zIndex = spriteRenderer.zIndex;
        topLeft.tint = spriteRenderer.tint;
        topLeft.rotation = spriteRenderer.rotation;
        topLeft.scale = new Vector2f(1.0f, 1.0f).mul(spriteRenderer.scale);

        drawSprite(topLeft);

        // Top middle.
        SpriteData topMiddle = new SpriteData(spriteRenderer.sprite.topMiddle, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.topLeft.getRegion().z * spriteRenderer.scale.x),
                realPosition.y));
        topMiddle.zIndex = spriteRenderer.zIndex;
        topMiddle.tint = spriteRenderer.tint;
        topMiddle.rotation = spriteRenderer.rotation;
        topMiddle.scale = new Vector2f(numWidthNeeded, 1.0f).mul(spriteRenderer.scale);

        drawSprite(topMiddle);

        // Top right.
        SpriteData topRight = new SpriteData(spriteRenderer.sprite.topRight, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.topLeft.getRegion().z * spriteRenderer.scale.x) +
                        numWidthNeeded * (spriteRenderer.sprite.topMiddle.getRegion().z * spriteRenderer.scale.x),
                realPosition.y));
        topRight.zIndex = spriteRenderer.zIndex;
        topRight.tint = spriteRenderer.tint;
        topRight.rotation = spriteRenderer.rotation;
        topRight.scale = new Vector2f(1.0f, 1.0f).mul(spriteRenderer.scale);

        drawSprite(topRight);

        // Middle left.
        SpriteData middleLeft = new SpriteData(spriteRenderer.sprite.middleLeft, new Vector2f(
                realPosition.x,
                realPosition.y + (spriteRenderer.sprite.topLeft.getRegion().w * spriteRenderer.scale.y)));
        middleLeft.zIndex = spriteRenderer.zIndex;
        middleLeft.tint = spriteRenderer.tint;
        middleLeft.rotation = spriteRenderer.rotation;
        middleLeft.scale = new Vector2f(1.0f, numHeightNeeded).mul(spriteRenderer.scale);

        drawSprite(middleLeft);

        // Middle.
        SpriteData middle = new SpriteData(spriteRenderer.sprite.middle, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.topLeft.getRegion().z * spriteRenderer.scale.x),
                realPosition.y + (spriteRenderer.sprite.topLeft.getRegion().w * spriteRenderer.scale.y)));
        middle.zIndex = spriteRenderer.zIndex;
        middle.tint = spriteRenderer.tint;
        middle.rotation = spriteRenderer.rotation;
        middle.scale = new Vector2f(numWidthNeeded, numHeightNeeded).mul(spriteRenderer.scale);

        drawSprite(middle);

        // Middle right.
        SpriteData middleRight = new SpriteData(spriteRenderer.sprite.middleRight, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.topLeft.getRegion().z * spriteRenderer.scale.x) +
                        numWidthNeeded * (spriteRenderer.sprite.topMiddle.getRegion().z * spriteRenderer.scale.x),
                realPosition.y + (spriteRenderer.sprite.topLeft.getRegion().w * spriteRenderer.scale.y)));
        middleRight.zIndex = spriteRenderer.zIndex;
        middleRight.tint = spriteRenderer.tint;
        middleRight.rotation = spriteRenderer.rotation;
        middleRight.scale = new Vector2f(1.0f, numHeightNeeded).mul(spriteRenderer.scale);

        drawSprite(middleRight);

        // Bottom left.
        SpriteData bottomLeft = new SpriteData(spriteRenderer.sprite.bottomLeft, new Vector2f(
                realPosition.x,
                realPosition.y + (spriteRenderer.sprite.bottomLeft.getRegion().w * spriteRenderer.scale.y) +
                        numHeightNeeded * (spriteRenderer.sprite.middleLeft.getRegion().w * spriteRenderer.scale.y)));
        bottomLeft.zIndex = spriteRenderer.zIndex;
        bottomLeft.tint = spriteRenderer.tint;
        bottomLeft.rotation = spriteRenderer.rotation;
        bottomLeft.scale = new Vector2f(1.0f, 1.0f).mul(spriteRenderer.scale);

        drawSprite(bottomLeft);

        // Bottom middle.
        SpriteData bottomMiddle = new SpriteData(spriteRenderer.sprite.bottomMiddle, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.bottomLeft.getRegion().z * spriteRenderer.scale.x),
                realPosition.y + (spriteRenderer.sprite.topLeft.getRegion().w * spriteRenderer.scale.y) +
                        numHeightNeeded * (spriteRenderer.sprite.middleLeft.getRegion().w * spriteRenderer.scale.y)));
        bottomMiddle.zIndex = spriteRenderer.zIndex;
        bottomMiddle.tint = spriteRenderer.tint;
        bottomMiddle.rotation = spriteRenderer.rotation;
        bottomMiddle.scale = new Vector2f(numWidthNeeded, 1.0f).mul(spriteRenderer.scale);

        drawSprite(bottomMiddle);

        // Bottom right.
        SpriteData bottomRight = new SpriteData(spriteRenderer.sprite.bottomRight, new Vector2f(
                realPosition.x + (spriteRenderer.sprite.bottomLeft.getRegion().z * spriteRenderer.scale.x) +
                        numWidthNeeded * (spriteRenderer.sprite.bottomMiddle.getRegion().z * spriteRenderer.scale.x),
                realPosition.y + (spriteRenderer.sprite.topRight.getRegion().w * spriteRenderer.scale.y) +
                        numHeightNeeded * (spriteRenderer.sprite.middleRight.getRegion().w * spriteRenderer.scale.y)));
        bottomRight.zIndex = spriteRenderer.zIndex;
        bottomRight.tint = spriteRenderer.tint;
        bottomRight.rotation = spriteRenderer.rotation;
        bottomRight.scale = new Vector2f(1.0f, 1.0f).mul(spriteRenderer.scale);

        drawSprite(bottomRight);
    }

    public void drawRectangle(RectangleData rectangleData) {
        if (rectangleData.filled) {
            drawTexture(
                    primitiveTexture,
                    rectangleData.position,
                    new Vector4f(0.0f, 0.0f, rectangleData.size.x, rectangleData.size.y),
                    rectangleData.zIndex,
                    rectangleData.color,
                    rectangleData.rotation,
                    rectangleData.scale,
                    rectangleData.origin);
        } else {
            Vector2f realSize = new Vector2f(
                    rectangleData.size.x * rectangleData.scale.x,
                    rectangleData.size.y * rectangleData.scale.y
            );
            Vector2f realPosition = new Vector2f(
                    rectangleData.position.x - rectangleData.scale.x * rectangleData.origin.x,
                    rectangleData.position.y - rectangleData.scale.y * rectangleData.origin.y
            );
            Vector2f rotationOrigin = new Vector2f(
                    realPosition.x + rectangleData.origin.x * rectangleData.scale.x,
                    realPosition.y + rectangleData.origin.y * rectangleData.scale.y
            );

            Vector2f position12 = MathUtil.rotatePosition(
                    new Vector2f(realPosition.x + realSize.x, realPosition.y),
                    rotationOrigin,
                    rectangleData.rotation
            );
            Vector2f position21 = new Vector2f(position12.x, position12.y);
            Vector2f position22 = MathUtil.rotatePosition(
                    new Vector2f(position12.x, position12.y + realSize.y),
                    position21,
                    rectangleData.rotation
            );
            Vector2f position31 = new Vector2f(position22.x, position22.y);
            Vector2f position32 = MathUtil.rotatePosition(
                    new Vector2f(position22.x + realSize.x, position22.y),
                    position31,
                    rectangleData.rotation + 180.0f
            );
            Vector2f position41 = new Vector2f(position32.x, position32.y);
            Vector2f position42 = MathUtil.rotatePosition(
                    new Vector2f(position32.x, position32.y + realSize.y),
                    position41,
                    rectangleData.rotation + 180.0f
            );

            LineData lineData1 = new LineData(position42, position12);
            lineData1.zIndex = rectangleData.zIndex;
            lineData1.color = rectangleData.color;
            lineData1.thickness = rectangleData.thickness;

            drawLine(lineData1);

            LineData lineData2 = new LineData(position21, position22);
            lineData2.zIndex = rectangleData.zIndex;
            lineData2.color = rectangleData.color;
            lineData2.thickness = rectangleData.thickness;

            drawLine(lineData2);

            LineData lineData3 = new LineData(position31, position32);
            lineData3.zIndex = rectangleData.zIndex;
            lineData3.color = rectangleData.color;
            lineData3.thickness = rectangleData.thickness;

            drawLine(lineData3);

            LineData lineData4 = new LineData(position41, position42);
            lineData4.zIndex = rectangleData.zIndex;
            lineData4.color = rectangleData.color;
            lineData4.thickness = rectangleData.thickness;

            drawLine(lineData4);
        }
    }

    public void drawPoint(PointData pointData) {
        drawTexture(
                primitiveTexture,
                pointData.position,
                new Vector4f(0.0f, 0.0f, 1.0f, 1.0f),
                pointData.zIndex,
                pointData.color,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f());
    }

    public void drawPoints(PointsData pointsData) {
        if (pointsData.points.size() < 2) {
            return;
        }

        for (int i = 1; i < pointsData.points.size(); i++) {
            Vector2f startPoint = pointsData.points.get(i - 1);
            Vector2f endPoint = pointsData.points.get(i);

            LineData lineData = new LineData(new Vector2f(
                    startPoint.x + pointsData.position.x, startPoint.y + pointsData.position.y),
                    new Vector2f(
                            endPoint.x + pointsData.position.x,
                            endPoint.y + pointsData.position.y)
            );
            lineData.zIndex = pointsData.zIndex;
            lineData.color = pointsData.color;
            lineData.thickness = pointsData.thickness;

            drawLine(lineData);
        }
    }

    public void drawLine(LineData lineData) {
        float length = lineData.point1.distance(lineData.point2);
        float rotation = (float)Math.atan2(
                lineData.point2.y - lineData.point1.y,
                lineData.point2.x - lineData.point1.x
        );

        drawTexture(
                primitiveTexture,
                lineData.point1,
                new Vector4f(0.0f, 0.0f, length, lineData.thickness),
                lineData.zIndex,
                lineData.color,
                (float)Math.toDegrees(rotation),
                new Vector2f(1.0f, 1.0f),
                new Vector2f()
        );
    }

    public void drawCircle(CircleData circleData) {
        PointsData pointsData = new PointsData(circleData.position,
                MathUtil.createCircle(circleData.radius, circleData.sides));
        pointsData.zIndex = circleData.zIndex;
        pointsData.color = circleData.color;
        pointsData.thickness = circleData.thickness;

        drawPoints(pointsData);
    }

    public void drawText(TextData textRenderer) {
        Font realFont;

        if (textRenderer.scale.x == textRenderer.scale.y) {
            realFont = Resources.loadFont(
                    textRenderer.font.getPath(), (int)(textRenderer.font.getSize() * textRenderer.scale.x));
        } else {
            realFont = textRenderer.font;
        }

        String[] lines = textRenderer.text.split("\n");

        Vector2f debugPosition = new Vector2f(
                textRenderer.position.x - textRenderer.scale.x * textRenderer.origin.x,
                textRenderer.position.y - textRenderer.scale.y * textRenderer.origin.y
        );
        Vector2f debugPosition2 = new Vector2f(
                textRenderer.position.x - textRenderer.scale.x * textRenderer.origin.x,
                textRenderer.position.y - textRenderer.scale.y * textRenderer.origin.y - realFont.getLineHeight(lines[0])
        );

        Vector4f viewRect1 = new Vector4f(
                textRenderer.position.x - textRenderer.scale.x * textRenderer.origin.x,
                textRenderer.position.y - textRenderer.scale.y * textRenderer.origin.y,
                realFont.getTextWidth(textRenderer.text),
                realFont.getTextHeight(textRenderer.text)
        );

        Vector2f realPosition = new Vector2f(debugPosition.x, debugPosition.y + realFont.getAscentHeight() - realFont.getDescentHeight() - 1f);

        int lineNumber = 0;

        for (String line : lines) {
            lineNumber++;

            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);

                if (c == ' ') {
                    realPosition.add(new Vector2f(realFont.getTextWidth(" "), 0));
                } else if (c == '\t') {
                    realPosition.add(new Vector2f(realFont.getTextWidth(" ") * 4, 0));
                } else if (c >= ' ') {
                    FontGlyphInfo fontGlyphInfo = realFont.getGlyphInfo(c);

                    drawGlyph(
                            new Vector2f(realPosition.x, realPosition.y),
                            realFont, fontGlyphInfo.position, fontGlyphInfo.region,
                            textRenderer.zIndex,
                            textRenderer.color,
                            textRenderer.rotation,
                            textRenderer.scale,
                            textRenderer.origin);

                    realPosition.add(new Vector2f(fontGlyphInfo.position.z + realFont.getKerning(), 0));
                }
            }

            if (line.trim().isEmpty()) {
                realPosition = new Vector2f(textRenderer.position.x, realPosition.y + realFont.getLineHeight("A") + realFont.getLineSpacing());
            } else {
                realPosition = new Vector2f(textRenderer.position.x, realPosition.y + realFont.getLineHeight(line) + realFont.getLineSpacing());
            }
        }

        if (Game.get().isDebugToolsEnabled() && Game.get().getDebugTools().isTextBoxEnabled()) {
            {
                RectangleData rectangleData = new RectangleData(new Vector2f(viewRect1.x - 3, viewRect1.y - 3), new Vector2f(viewRect1.z + 3, viewRect1.w + 3));
                rectangleData.zIndex = textRenderer.zIndex;
                rectangleData.filled = false;
                rectangleData.thickness = 2;
                rectangleData.color = new Color(1.0f, 1.0f, 0.0f, 1.0f);
                drawRectangle(rectangleData);
            }
        }
    }

    public void end() {
        if (size == 0) {
            return;
        }

        data.sort(Comparator.comparingInt(Data::getzIndex));
        renderer.draw();
    }

    private void drawTexture(Texture texture, Vector2f position, Vector4f region, int zIndex, Color tint, float angle,
                             Vector2f scale, Vector2f origin) {
        Data data = new Data();
        data.texture = texture;
        data.position = new Vector2f(position.x, position.y);
        data.dimensions = new Vector2f(region.z, region.w);
        data.region = new Vector4f(
                region.x / texture.getWidth(),
                region.y / texture.getHeight(),
                (region.x + region.z) / texture.getWidth(),
                (region.y + region.w) / texture.getHeight()
        );
        data.zIndex = zIndex;
        data.tint = tint.clone();
        data.rotation = angle;
        data.scale = new Vector2f(scale.x, scale.y);
        data.origin = new Vector2f(origin.x, origin.y);
        data.type = ElementType.Sprite;

        this.data.add(data);

        size++;
    }

    private void drawGlyph(Vector2f position, Font font, Vector4f characterPositions, Vector4f characterRegion,
                           int zIndex, Color color, float rotation, Vector2f scale, Vector2f origin) {
        Data data = new Data();
        data.texture = font.getTexture();
        // When drawing a glyph, position serve as a positionStart (glyph top left).
        data.position = new Vector2f(characterPositions.x, characterPositions.y);
        data.position.add(position);
        // When drawing a glyph, dimensions serve as a positionEnd (glyph bottom right).
        data.dimensions = new Vector2f(characterPositions.z, characterPositions.w);
        data.dimensions.add(position);
        data.scale = scale;
        data.origin = origin;
        data.region = characterRegion;
        data.tint = new Color(color.r, color.g, color.b, color.a);
        data.rotation = rotation;
        data.zIndex = zIndex;
        data.type = ElementType.TruetypeFont;

        this.data.add(data);

        size++;
    }

    private void clear() {
        size = 0;

        renderer.clear();
        data.clear();
    }
}
