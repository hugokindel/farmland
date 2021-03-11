package com.ustudents.engine.graphic;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;

public class RenderTarget {
    int fbo;
    int textureHandle;

    public RenderTarget() {
        fbo = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        textureHandle = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureHandle);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1280, 720, 0, GL_RGB, GL_UNSIGNED_BYTE, (ByteBuffer)null);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureHandle, 0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void destroy() {
        glDeleteFramebuffers(fbo);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            throw new IllegalStateException("Framebuffer not woring");
        }
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getHandle() {
        return fbo;
    }

    public int getTextureHandle() {
        return textureHandle;
    }
}
