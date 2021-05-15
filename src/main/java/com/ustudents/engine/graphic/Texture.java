package com.ustudents.engine.graphic;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.utility.FileUtil;

import org.lwjgl.system.*;

import java.nio.*;

import static com.ustudents.engine.core.Resources.getTexturesDirectory;
import static java.lang.Math.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.system.MemoryStack.*;

@Viewable
@JsonSerializable
public class Texture {
    private ByteBuffer data;

    private int width;

    private int height;

    private int numberOfComponents;

    private int handle;

    private boolean destroyed;

    @Viewable
    @JsonSerializable
    private String path;

    public Texture() {
        this.path = null;
        destroyed = false;
    }

    public Texture(String filePath) {
        this.path = filePath.replace(getTexturesDirectory() + "/", "");

        if (Game.get().canRender()) {
            loadTexture(filePath);
            handle = createTexture();
        }

        destroyed = false;
    }

    public Texture(ByteBuffer data, int width, int height, int numberOfComponents) {
        this.path = "from memory (byte buffer)";

        if (Game.get().canRender()) {
            loadTexture(data, width, height, numberOfComponents);
            handle = createTexture();
        }

        destroyed = false;
    }

    public Texture(byte[] data, int width, int height, int numberOfComponents) {
        this(byteArrayToBuffer(data), width, height, numberOfComponents);
    }

    @JsonSerializableConstructor
    public void deserialize() {
        if (Game.get().canRender()) {
            Texture texture = Resources.loadTexture(path);

            this.data = texture.data;
            this.width = texture.width;
            this.height = texture.height;
            this.numberOfComponents = texture.numberOfComponents;
            this.handle = texture.handle;
        }

        destroyed = false;
    }

    public void destroy() {
        if (!destroyed) {
            if (Game.get().canRender()) {
                glDeleteTextures(handle);
            }
            destroyed = true;
        }
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, handle);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getHandle() {
        return handle;
    }

    public String getPath() {
        return path;
    }

    public int getNumberOfComponents() {
        return numberOfComponents;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public ByteBuffer getData() {
        return data;
    }

    public void saveToPng(String filePath) {
        stbi_write_png(filePath, width, handle, numberOfComponents, data, 0);
    }

    private void loadTexture(String filePath) {
        ByteBuffer imageBuffer = FileUtil.readFile(filePath);
        assert imageBuffer != null;

        try (MemoryStack stack = stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer numberOfComponents = stack.mallocInt(1);
            data = stbi_load_from_memory(imageBuffer, width, height, numberOfComponents, 0);
            if (data == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }
            this.width = width.get(0);
            this.height = height.get(0);
            this.numberOfComponents = numberOfComponents.get(0);
        }
    }

    private void loadTexture(ByteBuffer data, int width, int height, int numberOfComponents) {
        this.data = data;
        this.width = width;
        this.height = height;
        this.numberOfComponents = numberOfComponents;
    }

    private int createTexture() {
        int id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        // TODO: Make customizable (for pixel textures and others).
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int format;
        if (numberOfComponents == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            format = GL_RGB;
            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
        } else if (numberOfComponents == 4) {
            premultiplyAlpha();
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            format = GL_RGBA;
            glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);
        } else {
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            format = GL_RED;
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width, height, 0, format, GL_UNSIGNED_BYTE, data);
        }

        stbi_image_free(data);

        return id;
    }

    private void premultiplyAlpha() {
        int stride = width * 4;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int i = y * stride + x * 4;
                float alpha = (data.get(i + 3) & 0xFF) / 255.0f;

                data.put(i, (byte)round(((data.get(i) & 0xFF) * alpha)));
                data.put(i + 1, (byte)round(((data.get(i + 1) & 0xFF) * alpha)));
                data.put(i + 2, (byte)round(((data.get(i + 2) & 0xFF) * alpha)));
            }
        }
    }

    public static ByteBuffer byteArrayToBuffer(byte[] array) {
        return (ByteBuffer) ByteBuffer.allocateDirect(array.length).order(ByteOrder.nativeOrder()).put(array).flip();
    }
}
