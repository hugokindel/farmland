package com.ustudents.farmland.graphics;

import com.ustudents.farmland.common.Resources;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.opengl.GL33.*;

public class SpriteBatch {
    public static class Element
    {
        Texture texture;
        Vector2f position;
        Vector2f dimensions;
        Vector2f origin;
        Vector2f scale;
        Vector4f region;
        Color color;
        float rotation;
        int layer;

        public int getLayer() {
            return layer;
        }
    }

    public class Renderer {
        FloatBuffer vertices;
        private final int vao;
        private final int vbo;
        private final int ebo;
        private boolean destroyed;

        public Renderer(int maxNumberOfSprites, Set<VertexVariable> attributes) {
            vertices = BufferUtils.createFloatBuffer(maxNumberOfSprites * 32);

            shader.bind();

            vao = glGenVertexArrays();
            vbo = glGenBuffers();
            ebo = glGenBuffers();

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

            destroyed = false;
        }

        public void destroy() {
            if (!destroyed) {
                glDeleteBuffers(vbo);
                glDeleteBuffers(ebo);
                glDeleteVertexArrays(vao);
                destroyed = true;
            }
        }

        public void clear() {
            vertices.clear();
        }

        public void putElement(SpriteBatch.Element element) {
            // Top left (0).
            putVertex(new Vector4f(
                    element.scale.x * element.position.x,
                    element.scale.y * element.position.y,
                    element.region.x,
                    element.region.y
            ), element.color);

            // Top right (1).
            putVertex(new Vector4f(
                    element.scale.x * (element.position.x + element.dimensions.x),
                    element.scale.y * element.position.y,
                    element.region.z,
                    element.region.y
            ), element.color);

            // Bottom left (2).
            putVertex(new Vector4f(
                    element.scale.x * element.position.x,
                    element.scale.y * (element.position.y  + element.dimensions.y),
                    element.region.x,
                    element.region.w
            ), element.color);

            // Bottom right (3).
            putVertex(new Vector4f(
                    element.scale.x * (element.position.x + element.dimensions.x),
                    element.scale.y * (element.position.y + element.dimensions.y),
                    element.region.z,
                    element.region.w
            ), element.color);
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

                glBindVertexArray(vao);

                // This happens within the VAO context.
                {
                    glBindBuffer(GL_ARRAY_BUFFER, vbo);
                    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, ebo);

                    for (int i = 0; i < size; i++) {
                        putElement(elements.get(i));
                    }

                    glBufferSubData(GL_ARRAY_BUFFER, 0, vertices.flip());

                    Texture last_texture = elements.get(0).texture;
                    int offset = 0;

                    for (int i = 0; i < size; i++)
                    {
                        Element sprite = elements.get(i);

                        if (sprite.texture != last_texture)
                        {
                            glBindTexture(GL_TEXTURE_2D, last_texture.getHandle());
                            glDrawElements(GL_TRIANGLES, (i - offset) * 6, GL_UNSIGNED_INT,
                                    (long)offset * 6 * 4);
                            offset = i;
                            last_texture = sprite.texture;
                        }
                    }

                    glBindTexture(GL_TEXTURE_2D, last_texture.getHandle());
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

    private final Shader shader;
    private final Renderer renderer;
    private final List<Element> elements;
    private int size;
    private Matrix4f projection;
    private float globalAlpha;
    private boolean shouldUpdateAlphaUniform;
    private boolean shouldUpdateMatrixUniform;
    private boolean shouldSortByLayer;
    private boolean destroyed;

    public SpriteBatch() {
        this(Objects.requireNonNull(Resources.loadShader("spritebatch")));
    }

    public SpriteBatch(Shader shader) {
        this.elements = new ArrayList<>(2048);
        this.shader = shader;
        this.renderer = new Renderer(2048, shader.getVertexAttributes());
        this.destroyed = false;
        this.projection = new Matrix4f();
    }

    public void destroy() {
        if (!destroyed) {
            renderer.destroy();
            destroyed = true;
        }
    }

    public void begin() {
        renderer.clear();
        elements.clear();
        size = 0;
        shouldSortByLayer = true;

        OrthoCamera c = new OrthoCamera(1000);
        c.setSize(1280, 720);

        if (!projection.equals(c.viewproj())) {
            shouldUpdateMatrixUniform = true;
            projection = c.viewproj();
        }

        if (globalAlpha != 1.0f) {
            shouldUpdateAlphaUniform = true;
            globalAlpha = 1.0f;
        }
    }

    public void draw(Texture texture, Vector4i region, float x, float y, int layer) {
        Element element = new Element();

        element.texture = texture;
        element.position = new Vector2f(x, y);
        element.dimensions = new Vector2f(region.z, region.w);
        element.scale = new Vector2f(1.0f, 1.0f);
        element.origin = new Vector2f(0.0f, 0.0f);
        element.region = new Vector4f(
                (float)region.x / texture.getWidth(), (float)region.y / texture.getHeight(),
                (float)(region.x + region.z) / texture.getWidth(), (float)(region.y + region.w) / texture.getHeight()
        );
        element.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        element.rotation = 0.0f;
        element.layer = layer;

        elements.add(element);
        size++;
    }

    public void end() {
        if (size == 0) {
            return;
        }

        if (shouldSortByLayer) {
            elements.sort(Comparator.comparingInt(Element::getLayer));
        }

        renderer.draw();
    }
}
