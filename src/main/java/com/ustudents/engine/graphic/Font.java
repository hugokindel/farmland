package com.ustudents.engine.graphic;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import com.ustudents.engine.utility.FileUtil;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.*;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static com.ustudents.engine.core.Resources.getFontsDirectory;
import static com.ustudents.engine.core.Resources.getTexturesDirectory;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.stackPush;

@JsonSerializable
public class Font {
    public static class GlyphInfo {
        Vector4f position;
        Vector4f region;
    }

    private static final String defaultCharacterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
            "0123456789.,;:?!-_~#\"'&()[]{}^|`/\\@°+=*%€$<>ÀÁÂÄÆÇÈÉÊËÌÍÎÏÒÓÔÖŒÙÚÛÜàáâäæçèéêëìíîïòóôöœùúûü ";

    private Texture texture;

    private ByteBuffer data;

    private STBTTPackedchar.Buffer characterData;

    private Map<Character, GlyphInfo> glyphInfoPerCharacter;

    private boolean destroyed;

    private STBTTFontinfo info;

    private int kerning;

    private int ascent;

    private int descent;

    private int lineGap;

    private Map<String, Float> widthPerText;

    private Map<String, Float> heightPerText;

    public float averageHeight;

    @JsonSerializable
    private Integer fontSize;

    @JsonSerializable
    String path;

    public Font(String filePath, int fontSize) {
        this.path = filePath.replace(getFontsDirectory() + "/", "");
        this.fontSize = fontSize;
        glyphInfoPerCharacter = new HashMap<>();
        loadFont(filePath);
        widthPerText = new HashMap<>();
        heightPerText = new HashMap<>();
    }

    @JsonSerializableConstructor
    private void fromJson() {
        glyphInfoPerCharacter = new HashMap<>();
        loadFont(getFontsDirectory() + "/" + path);
        widthPerText = new HashMap<>();
        heightPerText = new HashMap<>();
    }

    public void destroy() {
        if (!destroyed) {
            texture.destroy();
            destroyed = true;
        }
    }

    private void loadFont(String filePath) {
        loadFont(filePath, defaultCharacterSet);
    }

    private void loadFont(String filePath, String characterSet) {
        kerning = 1;

        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
            data = FileUtil.readFile(filePath);
            ByteBuffer bitmap = BufferUtils.createByteBuffer(1024 * 1024);
            characterData = STBTTPackedchar.malloc(8333);
            if (!stbtt_PackBegin(pc, bitmap, 1024, 1024, 0, 1, MemoryUtil.NULL)) {
                throw new IllegalStateException("Failed to initialize font");
            }
            if (!stbtt_PackFontRange(pc, data, 0, fontSize, ' ', characterData)) {
                throw new IllegalStateException("Failed to pack font");
            }
            stbtt_PackEnd(pc);
            texture = new Texture(bitmap, 1024, 1024, 1);
        }

        for (int i = 0; i < characterSet.length(); i++) {
            char c = characterSet.charAt(i);
            glyphInfoPerCharacter.put(c, makeGlyphInfo(c));
        }

        info = STBTTFontinfo.create();

        if (!stbtt_InitFont(info, data)) {
            throw new IllegalStateException("Failed to load font information");
        }

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent  = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(info, pAscent, pDescent, pLineGap);

            ascent = pAscent.get(0);
            descent = pDescent.get(0);
            lineGap = pLineGap.get(0);
            averageHeight = (ascent - descent + lineGap) * stbtt_ScaleForPixelHeight(info, fontSize) / 2;
        }
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public Texture getTexture() {
        return texture;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getSize() {
        return fontSize;
    }

    public int getAscent() {
        return ascent;
    }

    public int getDescent() {
        return descent;
    }

    public int getLineGap() {
        return lineGap;
    }

    public int getKerning() {
        return kerning;
    }

    public String getPath() {
        return path;
    }

    public GlyphInfo getGlyphInfo(char c) {
        return glyphInfoPerCharacter.get(c);
    }

    // Implementation from: https://github.com/LWJGL/lwjgl3/blob/master/modules/samples/src/test/java/org/lwjgl/demo/stb/Truetype.java
    public float getTextWidth(String text) {
        if (widthPerText.containsKey(text)) {
            return widthPerText.get(text);
        }

        float maxWidth = 0;

        for (String line : text.split("\n")) {
            int width = 0;

            try (MemoryStack stack = stackPush()) {
                IntBuffer pCodePoint = stack.mallocInt(1);
                IntBuffer pAdvancedWidth = stack.mallocInt(1);
                IntBuffer pLeftSideBearing = stack.mallocInt(1);

                int i = 0;
                int to = line.length();
                while (i < to) {
                    i += getCharacterCodepointMetrics(line, to, i, pCodePoint);
                    int cp = pCodePoint.get(0);
                    stbtt_GetCodepointHMetrics(info, cp, pAdvancedWidth, pLeftSideBearing);
                    width += pAdvancedWidth.get(0);
                }
            }

            float realWidth = (width * stbtt_ScaleForPixelHeight(info, fontSize)) - 1;

            if (realWidth > maxWidth) {
                maxWidth = realWidth;
            }
        }

        widthPerText.put(text, maxWidth);

        return maxWidth;
    }

    public float getTextHeight(String text) {
        if (heightPerText.containsKey(text)) {
            return heightPerText.get(text);
        }
        //return (ascent - descent + lineGap) * stbtt_ScaleForPixelHeight(info, fontSize);
        long numNewLines = text.chars().filter(l -> l == '\n').count();
        float height = averageHeight * (numNewLines + 1) + (numNewLines > 0 ? fontSize * (numNewLines - 1) : 0);
        heightPerText.put(text, height);
        return height;
    }

    public float getAverageTextHeight() {
        return averageHeight;
    }

    public float getBiggestCharacterHeight(String text) {
        String firstLine = text.split("\n")[0];
        float max = -getGlyphInfo(firstLine.charAt(0)).position.y;

        for (int i = 1; i < firstLine.length(); i++) {
            float height = -getGlyphInfo(firstLine.charAt(i)).position.y;

            if (height > max) {
                max = height;
            }
        }

        return max;
    }

    private GlyphInfo makeGlyphInfo(char c) {
        try (MemoryStack stack = stackPush()) {
            STBTTAlignedQuad q = STBTTAlignedQuad.mallocStack(stack);
            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(0.0f);
            GlyphInfo info = new GlyphInfo();

            stbtt_GetPackedQuad(characterData, 1024, 1024, c - ' ', x, y, q, true);

            info.position = new Vector4f(q.x0(), q.y0(), q.x1(), q.y1());
            info.region = new Vector4f(q.s0(), q.t0(), q.s1(), q.t1());

            return info;
        }
    }

    private static int getCharacterCodepointMetrics(String text, int to, int i, IntBuffer cpOut) {
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
}
