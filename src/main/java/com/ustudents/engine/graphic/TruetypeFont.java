package com.ustudents.engine.graphic;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.utility.FileUtil;
import org.joml.Vector2f;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class TruetypeFont {
    public static class GlyphInfo {
        Vector2f[] pos;
        Vector2f[] uvs;
        Vector2f offset;
    }

    public static class GlyphInfo2 {
        Vector2f start;
        Vector2f length;
        Vector2f offset;
    }

    public static class AlignedQuad {
        float x0;
        float x1;
        float y0;
        float y1;
        float s0;
        float s1;
        float t0;
        float t1;
    }

    private Texture texture;

    private boolean destroyed;

    private ByteBuffer ttf;

    private STBTTFontinfo info;

    private int ascent;

    private int descent;

    private int lineGap;

    private STBTTBakedChar.Buffer cdata;

    public TruetypeFont(String filePath) {
        loadFont(filePath);
    }

    public void destroy() {
        if (!destroyed) {
            texture.destroy();
            destroyed = true;
        }
    }

    private void loadFont(String filePath) {
        ttf = FileUtil.readFile(filePath);
        info = STBTTFontinfo.create();
        if (!stbtt_InitFont(info, ttf)) {
            throw new IllegalStateException("Failed to initialize font.");
        }
        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
        }

        STBTTBakedChar.Buffer cdata = STBTTBakedChar.malloc(96);

        ByteBuffer bitmap = BufferUtils.createByteBuffer(512 * 512);
        stbtt_BakeFontBitmap(ttf, 24, bitmap, 512, 512, 32, cdata);
        texture = new Texture(bitmap, 512, 512, 1);
        this.cdata = cdata;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    private static int getCP(String text, int to, int i, IntBuffer cpOut) {
        char c1 = text.charAt(i);
        if (Character.isHighSurrogate(c1) && i + 1 < to) {
            char c2 = text.charAt(i + 1);
            if (Character.isLowSurrogate(c2)) {
                cpOut.put(0, Character.toCodePoint(c1, c2));
                return 2;
            }
        }
        cpOut.put(0, c1);
        return 1;
    }

    public Texture getTexture() {
        return texture;
    }

    public void render(String text, Spritebatch spritebatch) {
        spritebatch.begin();

        try (MemoryStack stack = stackPush()) {
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);

            for (int i = 0; i < text.length(); i++) {
                stbtt_GetBakedQuad(cdata, 512, 512, text.charAt(i) - ' ', x, y, q, true);

                GlyphInfo info = makeGlyphInfo(text.charAt(i), new Vector2f(0, 0));
                spritebatch.drawFont("t", this, new AlignedQuad() {{
                    x0 = q.x0();
                    x1 = q.x1();
                    y0 = q.y0();
                    y1 = q.y1();
                    s0 = q.s0();
                    s1 = q.s1();
                    t0 = q.t0();
                    t1 = q.t1();
                }});
            }
        }

        spritebatch.end();
    }

    GlyphInfo makeGlyphInfo(int character, Vector2f offset) {
        try (MemoryStack stack = stackPush()) {
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);
            stbtt_GetBakedQuad(cdata, 512, 512, character - ' ', x, y, q, true);
            GlyphInfo info = new GlyphInfo();
            float xmin = q.x0();
            float xmax = q.x1();
            float ymin = -q.y1();
            float ymax = -q.y0();
            info.offset = new Vector2f(0, 0);
            info.pos = new Vector2f[] {
                    new Vector2f(xmin, ymin),
                    new Vector2f(xmin, ymax),
                    new Vector2f(xmax, ymax),
                    new Vector2f(xmax, ymin)
            };
            info.uvs = new Vector2f[] {
                    new Vector2f(q.s0(), q.t1()),
                    new Vector2f(q.s0(), q.t0()),
                    new Vector2f(q.s1(), q.t0()),
                    new Vector2f(q.s1(), q.t1())
            };
            return info;
        }
    }

    GlyphInfo2 makeGlyphInfo2(int character, Vector2f offset) {
        try (MemoryStack stack = stackPush()) {
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);
            stbtt_GetBakedQuad(cdata, 512, 512, character - ' ', x, y, q, true);
            GlyphInfo2 info = new GlyphInfo2();
            float xmin = q.x0();
            float xmax = q.x1();
            float ymin = -q.y1();
            float ymax = -q.y0();
            info.offset = new Vector2f(0, 0);
            info.start = new Vector2f(q.x0(), -q.y0());
            info.length = new Vector2f(q.x1(), -q.y1());
            return info;
        }
    }
}
