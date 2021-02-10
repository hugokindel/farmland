package com.ustudents.engine.graphic;

import com.ustudents.engine.utility.FileUtil;

import org.lwjgl.system.*;

import java.nio.*;

import static java.lang.Math.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryStack.*;

public class Texture {
    private ByteBuffer data;
    private int width;
    private int height;
    private int numberOfComponents;
    private final int handle;
    private boolean destroyed;

    public Texture(String filePath) {
        loadTexture(filePath);
        handle = createTexture();
        destroyed = false;
    }

    public void destroy() {
        if (!destroyed) {
            glDeleteTextures(handle);
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

    private int createTexture() {
        int id = glGenTextures();

        glBindTexture(GL_TEXTURE_2D, id);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int format;
        if (numberOfComponents == 3) {
            if ((width & 3) != 0) {
                glPixelStorei(GL_UNPACK_ALIGNMENT, 2 - (width & 1));
            }
            format = GL_RGB;
        } else {
            premultiplyAlpha();
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            format = GL_RGBA;
        }

        glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, data);

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
}
