package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.cli.print.Out;
import org.joml.*;
import org.lwjgl.BufferUtils;

import java.lang.Math;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL33.*;

@SuppressWarnings({"unused"})
public class Spritebatch {
    public enum ElementType {
        Sprite,
        TruetypeFont
    }

    public static class Element
    {
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
                    IntBuffer indiceArray = BufferUtils.createIntBuffer(maxNumberOfSprites * 6);

                    for (int i = 0; i < maxNumberOfSprites; ++i) {
                        for (int j = 0; j < 6; ++j) {
                            indiceArray.put(i * 6 + j, indices[j] + i * 4);
                        }
                    }

                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);
                    glBufferData(GL_ELEMENT_ARRAY_BUFFER, indiceArray, GL_DYNAMIC_DRAW);

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

        public void putElement(Spritebatch.Element element) {
            if (element.type == ElementType.Sprite) {
                element.position = new Vector2f(element.position.x - element.origin.x, element.position.y - element.origin.y);

                Vector2f position1 = new Vector2f(element.scale.x * (element.position.x),
                        element.scale.y * element.position.y);
                Vector2f position2 = new Vector2f(element.scale.x * (element.position.x + element.dimensions.x),
                        element.scale.y * element.position.y);
                Vector2f position3 = new Vector2f(element.scale.x * element.position.x,
                        element.scale.y * (element.position.y  + element.dimensions.y));
                Vector2f position4 = new Vector2f(element.scale.x * (element.position.x + element.dimensions.x),
                        element.scale.y * (element.position.y + element.dimensions.y));


                if (element.rotation != 0.0f) {
                    Vector2f rotationOrigin = new Vector2f(position1.x + (element.origin.x * element.scale.x), position1.y + (element.origin.y * element.scale.y));

                    position1 = rotatePixel(position1, rotationOrigin, element.rotation);
                    position2 = rotatePixel(position2, rotationOrigin, element.rotation);
                    position3 = rotatePixel(position3, rotationOrigin, element.rotation);
                    position4 = rotatePixel(position4, rotationOrigin, element.rotation);
                }

                // Top left (0).
                putVertex(new Vector4f(
                        position1.x,
                        position1.y,
                        element.region.x,
                        element.region.y
                ), element.tint);

                // Top right (1).
                putVertex(new Vector4f(
                        position2.x,
                        position2.y,
                        element.region.z,
                        element.region.y
                ), element.tint);

                // Bottom left (2).
                putVertex(new Vector4f(
                        position3.x,
                        position3.y,
                        element.region.x,
                        element.region.w
                ), element.tint);

                // Bottom right (3).
                putVertex(new Vector4f(
                        position4.x,
                        position4.y,
                        element.region.z,
                        element.region.w
                ), element.tint);
            } else {
                // Top left (0).
                putVertex(new Vector4f(
                        element.position.x,
                        element.position.y,
                        element.region.x,
                        element.region.y
                ), element.tint);

                // Top right (1).
                putVertex(new Vector4f(
                        element.dimensions.x,
                        element.position.y,
                        element.region.z,
                        element.region.y
                ), element.tint);

                // Bottom left (2).
                putVertex(new Vector4f(
                        element.position.x,
                        element.dimensions.y,
                        element.region.x,
                        element.region.w
                ), element.tint);

                // Bottom right (3).
                putVertex(new Vector4f(
                        element.dimensions.x,
                        element.dimensions.y,
                        element.region.z,
                        element.region.w
                ), element.tint);
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
                if (shouldUpdateAlphaUniform) {
                    shader.setUniform1f("alpha", globalAlpha);
                    shouldUpdateAlphaUniform = false;
                }

                if (shouldUpdateMatrixUniform) {
                    shader.setUniformMatrix4fv("projection", projection);
                    shouldUpdateMatrixUniform = false;
                }

                shader.setUniform1i("type",  elements.get(0).getType());

                glBindVertexArray(vao);

                // This happens within the VAO context.
                {
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

                    for (int i = 0; i < size; i++) {
                        putElement(elements.get(i));
                    }

                    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices.flip());

                    Texture lastTexture = elements.get(0).texture;
                    int lastType = elements.get(0).getType();
                    int offset = 0;

                    for (int i = 0; i < size; i++) {
                        Element sprite = elements.get(i);

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

    private static final Map<String, List<Vector2f>> circleCache = new HashMap<>();
    private final Shader shader;
    private final Renderer renderer;
    private final List<Element> elements;
    private final Camera camera;
    private int size;
    private Matrix4f projection;
    private float globalAlpha;
    private boolean shouldUpdateAlphaUniform;
    private boolean shouldUpdateMatrixUniform;
    private boolean shouldSortByLayer;
    private boolean destroyed;
    public Texture whiteTexture;

    public Spritebatch() {
        this(Game.get().getSceneManager().getScene().getCamera());
    }

    public Spritebatch(Camera camera) {
        this(camera, Objects.requireNonNull(Resources.loadShader("spritebatch")));
    }

    public Spritebatch(Camera camera, Shader shader) {
        this.elements = new ArrayList<>(2048);
        this.shader = shader;
        this.camera = camera;
        this.renderer = new Renderer(2048, shader.getVertexAttributes());
        this.destroyed = false;
        this.projection = new Matrix4f();
        this.whiteTexture = new Texture(new byte[] {(byte)255, (byte)255, (byte)255, (byte)255}, 1, 1, 4);

        if (Game.isDebugging()) {
            Out.printlnDebug("Spritebatch created.");
        }
    }

    public void destroy() {
        if (!destroyed) {
            renderer.destroy();
            whiteTexture.destroy();
            destroyed = true;

            if (Game.isDebugging()) {
                Out.printlnDebug("Spritebatch destroyed.");
            }
        }
    }

    public void begin() {
        renderer.clear();
        elements.clear();
        size = 0;
        shouldSortByLayer = true;

        if (!projection.equals(camera.getViewProjectionMatrix())) {
            shouldUpdateMatrixUniform = true;
            projection = new Matrix4f();
            camera.getViewProjectionMatrix().get(projection);
        }

        if (globalAlpha != 1.0f) {
            shouldUpdateAlphaUniform = true;
            globalAlpha = 1.0f;
        }
    }

    public void draw(Texture texture) {
        draw(
                texture,
                new Vector2f(0.0f, 0.0f),
                new Vector4f(0, 0, texture.getWidth(), texture.getHeight()),
                0,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position) {
        draw(
                texture,
                position,
                new Vector4f(0, 0, texture.getWidth(), texture.getHeight()),
                0,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region) {
        draw(
                texture,
                position,
                region,
                0,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region, int zIndex) {
        draw(
                texture,
                position,
                region,
                zIndex,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region, int zIndex, Color tint) {
        draw(
                texture,
                position,
                region,
                zIndex,
                tint,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region, int zIndex, Color tint, float rotation) {
        draw(
                texture,
                position,
                region,
                zIndex,
                tint,
                rotation,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region, int zIndex, Color tint, float rotation, Vector2f scale) {
        draw(
                texture,
                position,
                region,
                zIndex,
                tint,
                rotation,
                scale,
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void draw(Texture texture, Vector2f position, Vector4f region, int zIndex, Color tint, float rotation, Vector2f scale, Vector2f origin) {
        Element element = new Element();

        element.texture = texture;
        element.position = new Vector2f(position.x, position.y);
        element.dimensions = new Vector2f(region.z, region.w);
        element.region = new Vector4f(
                region.x / texture.getWidth(), region.y / texture.getHeight(),
                (region.x + region.z) / texture.getWidth(), (region.y + region.w) / texture.getHeight()
        );
        element.zIndex = zIndex;
        element.tint = tint.clone();
        element.rotation = rotation;
        element.scale = new Vector2f(scale.x, scale.y);
        element.origin = new Vector2f(origin.x, origin.y);
        element.type = ElementType.Sprite;

        elements.add(element);
        size++;
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size) {
        drawFilledRectangle(
                position,
                size,
                0,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size, int zIndex) {
        drawFilledRectangle(
                position,
                size,
                zIndex,
                Color.WHITE,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size, int zIndex, Color color) {
        drawFilledRectangle(
                position,
                size,
                zIndex,
                color,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size, int zIndex, Color color, float rotation) {
        drawFilledRectangle(
                position,
                size,
                zIndex,
                color,
                rotation,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size, int zIndex, Color color, float rotation, Vector2f scale) {
        drawFilledRectangle(
                position,
                size,
                zIndex,
                color,
                rotation,
                scale,
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawFilledRectangle(Vector2f position, Vector2f size, int zIndex, Color color, float rotation, Vector2f scale, Vector2f origin) {
        draw(
                whiteTexture,
                position,
                new Vector4f(0.0f, 0.0f, size.x, size.y),
                zIndex,
                color,
                rotation,
                scale,
                origin
        );
    }

    public void drawRectangle(Vector2f position, Vector2f size) {
        drawRectangle(
                position,
                size,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawRectangle(Vector2f position, Vector2f size, int zIndex) {
        drawRectangle(
                position,
                size,
                zIndex,
                Color.WHITE,
                1
        );
    }

    public void drawRectangle(Vector2f position, Vector2f size, int zIndex, Color color) {
        drawRectangle(
                position,
                size,
                zIndex,
                color,
                1
        );
    }

    public void drawRectangle(Vector2f position, Vector2f size, int zIndex, Color color, float thickness) {
        drawLine(new Vector2f(position.x, position.y), new Vector2f(size.x, position.y), zIndex, color, thickness);
        drawLine(new Vector2f(position.x + 1.0f, position.y), new Vector2f(position.x + 1.0f, size.y + thickness), zIndex, color, thickness);
        drawLine(new Vector2f(position.x, size.y), new Vector2f(size.x, size.y), zIndex, color, thickness);
        drawLine(new Vector2f(size.x + 1.0f, position.y), new Vector2f(size.x + 1.0f, size.y + thickness), zIndex, color, thickness);
    }

    public void drawPoint(Vector2f position) {
        drawPoint(
                position,
                0,
                Color.WHITE
        );
    }

    public void drawPoint(Vector2f position, int zIndex) {
        drawPoint(
                position,
                zIndex,
                Color.WHITE
        );
    }

    public void drawPoint(Vector2f position, int zIndex, Color color) {
        draw(
                whiteTexture,
                position,
                new Vector4f(0.0f, 0.0f, 1.0f, 1.0f),
                zIndex,
                color,
                0.0f,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawPoints(Vector2f position, List<Vector2f> points) {
        drawPoints(
                position,
                points,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawPoints(Vector2f position, List<Vector2f> points, int zIndex) {
        drawPoints(
                position,
                points,
                zIndex,
                Color.WHITE,
                1
        );
    }

    public void drawPoints(Vector2f position, List<Vector2f> points, int zIndex, Color color) {
        drawPoints(
                position,
                points,
                zIndex,
                color,
                1
        );
    }

    public void drawPoints(Vector2f position, List<Vector2f> points, int zIndex, Color color, float thickness) {
        if (points.size() < 2) {
            return;
        }

        for (int i = 1; i < points.size(); i++) {
            Vector2f startPoint = points.get(i - 1);
            Vector2f endPoint = points.get(i);

            drawLine(
                    new Vector2f(startPoint.x + position.x, startPoint.y + position.y),
                    new Vector2f(endPoint.x + position.x, endPoint.y + position.y),
                    zIndex,
                    color,
                    thickness);
        }
    }

    public void drawLine(Vector2f point1, Vector2f point2) {
        drawLine(
                point1,
                point2,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawLine(Vector2f point1, Vector2f point2, int zIndex) {
        drawLine(
                point1,
                point2,
                zIndex,
                Color.WHITE,
                1
        );
    }

    public void drawLine(Vector2f point1, Vector2f point2, int zIndex, Color color) {
        drawLine(
                point1,
                point2,
                zIndex,
                color,
                1
        );
    }

    public void drawLine(Vector2f point1, Vector2f point2, int zIndex, Color color, float thickness) {
        float length = point1.distance(point2);
        float rotation = (float)Math.atan2(point2.y - point1.y, point2.x - point1.x);

        drawLine(
                point1,
                length,
                (float)Math.toDegrees(rotation),
                zIndex,
                color,
                thickness
        );
    }

    public void drawLine(Vector2f position, float length) {
        drawLine(
                position,
                length,
                0,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawLine(Vector2f position, float length, float rotation) {
        drawLine(
                position,
                length,
                rotation,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawLine(Vector2f position, float length, float rotation, int zIndex) {
        drawLine(
                position,
                length,
                rotation,
                zIndex,
                Color.WHITE,
                1
        );
    }

    public void drawLine(Vector2f position, float length, float rotation, int zIndex, Color color) {
        drawLine(
                position,
                length,
                rotation,
                zIndex,
                color,
                1
        );
    }

    public void drawLine(Vector2f position, float length, float rotation, int zIndex, Color color, float thickness) {
        draw(
                whiteTexture,
                position,
                new Vector4f(0.0f, 0.0f, length, thickness),
                zIndex,
                color,
                rotation,
                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f, 0.0f)
        );
    }

    public void drawCircle(Vector2f center, float radius, int sides) {
        drawCircle(
                center,
                radius,
                sides,
                0,
                Color.WHITE,
                1
        );
    }

    public void drawCircle(Vector2f center, float radius, int sides, int zIndex) {
        drawCircle(
                center,
                radius,
                sides,
                zIndex,
                Color.WHITE,
                1
        );
    }

    public void drawCircle(Vector2f center, float radius, int sides, int zIndex, Color color) {
        drawCircle(
                center,
                radius,
                sides,
                zIndex,
                color,
                1
        );
    }

    public void drawCircle(Vector2f center, float radius, int sides, int zIndex, Color color, int thickness) {
        drawPoints(
                center,
                createCircle(radius, sides),
                zIndex,
                color,
                thickness
        );
    }

    public void drawText(String text, Vector2f position, Font font) {
        Vector2f realPosition = new Vector2f(position.x, position.y);

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == ' ') {
                realPosition.add(new Vector2f(font.getTextWidth(" "), 0));
            } else if (c == '\n') {
                realPosition = new Vector2f(position.x, realPosition.y + font.getSize());
            } else if (c == '\t') {
                realPosition.add(new Vector2f(font.getTextWidth(" ") * 4, 0));
            } else {
                Font.GlyphInfo glyphInfo = font.getGlyphInfo(c);
                drawGlyph(realPosition, glyphInfo.position, glyphInfo.region, font);
                realPosition.add(new Vector2f(glyphInfo.position.z + font.getKerning(), 0));
            }
        }
    }

    public void end() {
        if (size == 0) {
            return;
        }

        if (shouldSortByLayer) {
            elements.sort(Comparator.comparingInt(Element::getzIndex));
        }

        renderer.draw();
    }

    private void drawGlyph(Vector2f position, Vector4f characterPositions, Vector4f characterRegion, Font font) {
        Element element = new Element();

        element.texture = font.getTexture();
        // When drawing a glyph, position serve as a positionStart
        element.position = new Vector2f(characterPositions.x, characterPositions.y);
        element.position.add(position);
        // When drawing a glyph, dimensions serve as a positionEnd
        element.dimensions = new Vector2f(characterPositions.z, characterPositions.w);
        element.dimensions.add(position);
        element.scale = new Vector2f(1.0f, 1.0f);
        element.origin = new Vector2f(0.0f, 0.0f);
        element.region = characterRegion;
        element.tint = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        element.rotation = 0.0f;
        element.zIndex = 0;
        element.type = ElementType.TruetypeFont;

        elements.add(element);
        size++;
    }

    private static Vector2f rotatePixel(Vector2f position, Vector2f origin, float angle) {
        double angleInRad = toRadians(angle);
        Vector2f translation = new Vector2f(position.x - origin.x, position.y - origin.y);
        Vector2f rotation = new Vector2f(
                (float)(translation.x * cos(angleInRad) - translation.y * sin(angleInRad)),
                (float)(translation.x * sin(angleInRad) + translation.y * cos(angleInRad))
        );

        return new Vector2f(rotation.x + origin.x, rotation.y + origin.y);
    }

    private static List<Vector2f> createCircle(float radius, int sides) {
        String circleKey = radius + "x" + sides;

        if (circleCache.containsKey(circleKey)) {
            return circleCache.get(circleKey);
        }

        List<Vector2f> vectors = new ArrayList<>();
        double max = 2.0 * PI;
        double step = max / sides;

        for (double theta = 0.0f; theta < max; theta += step) {
            vectors.add(new Vector2f((float)(radius * cos(theta)), (float)(radius * sin(theta))));
        }

        vectors.add(new Vector2f((float)(radius * cos(0)), (float)(radius * sin(0))));

        circleCache.put(circleKey, vectors);

        return vectors;
    }
}
