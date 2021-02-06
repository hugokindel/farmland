package com.ustudents.farmland.graphics;

import com.ustudents.farmland.cli.print.Out;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL32.*;

@SuppressWarnings({"unused"})
public class Shader {
    private int programHandle;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;
    private Map<String, VertexVariable> vertexAttributes;
    private Map<String, VertexVariable> uniformVariables;

    public Shader(String vertexShader, String fragmentShader) {
        compileProgram(vertexShader, fragmentShader);
    }

    public void destroy() {
        destroyProgram();
    }

    public void bind() {
        glUseProgram(programHandle);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public int getProgramHandle() {
        return programHandle;
    }

    public int getVertexShaderHandle() {
        return vertexShaderHandle;
    }

    public int getFragmentShaderHandle() {
        return fragmentShaderHandle;
    }

    public Map<String, VertexVariable> getVertexAttributes() {
        return vertexAttributes;
    }

    public Map<String, VertexVariable> getUniformVariables() {
        return uniformVariables;
    }

    public void setUniform1i(String name, int value) {
        glUniform1i(uniformVariables.get(name).location, value);
    }

    public void setUniform1i(int location, int value) {
        glUniform1i(location, value);
    }

    public void setUniform2i(String name, int value1, int value2) {
        glUniform2i(uniformVariables.get(name).location, value1, value2);
    }

    public void setUniform2i(int location, int value1, int value2) {
        glUniform2i(location, value1, value2);
    }

    public void setUniform2i(String name, Vector2i value) {
        glUniform2i(uniformVariables.get(name).location, value.x, value.y);
    }

    public void setUniform2i(int location, Vector2i value) {
        glUniform2i(location, value.x, value.y);
    }

    public void setUniform3i(String name, int value1, int value2, int value3) {
        glUniform3i(uniformVariables.get(name).location, value1, value2, value3);
    }

    public void setUniform3i(int location, int value1, int value2, int value3) {
        glUniform3i(location, value1, value2, value3);
    }

    public void setUniform3i(String name, Vector3i value) {
        glUniform3i(uniformVariables.get(name).location, value.x, value.y, value.z);
    }

    public void setUniform3i(int location, Vector3i value) {
        glUniform3i(location, value.x, value.y, value.z);
    }

    public void setUniform4i(String name, int value1, int value2, int value3, int value4) {
        glUniform4i(uniformVariables.get(name).location, value1, value2, value3, value4);
    }

    public void setUniform4i(int location, int value1, int value2, int value3, int value4) {
        glUniform4i(location, value1, value2, value3, value4);
    }

    public void setUniform4i(String name, Vector4i value) {
        glUniform4i(uniformVariables.get(name).location, value.x, value.y, value.z, value.w);
    }

    public void setUniform4i(int location, Vector4i value) {
        glUniform4i(location, value.x, value.y, value.z, value.w);
    }

    public void setUniform1f(String name, float value) {
        glUniform1f(uniformVariables.get(name).location, value);
    }

    public void setUniform1f(int location, float value) {
        glUniform1f(location, value);
    }

    public void setUniform2f(String name, float value1, float value2) {
        glUniform2f(uniformVariables.get(name).location, value1, value2);
    }

    public void setUniform2f(int location, float value1, float value2) {
        glUniform2f(location, value1, value2);
    }

    public void setUniform2f(String name, Vector2f value) {
        glUniform2f(uniformVariables.get(name).location, value.x, value.y);
    }

    public void setUniform2f(int location, Vector2f value) {
        glUniform2f(location, value.x, value.y);
    }

    public void setUniform3f(String name, float value1, float value2, float value3) {
        glUniform3f(uniformVariables.get(name).location, value1, value2, value3);
    }

    public void setUniform3f(int location, float value1, float value2, float value3) {
        glUniform3f(location, value1, value2, value3);
    }

    public void setUniform3f(String name, Vector3f value) {
        glUniform3f(uniformVariables.get(name).location, value.x, value.y, value.z);
    }

    public void setUniform3f(int location, Vector3f value) {
        glUniform3f(location, value.x, value.y, value.z);
    }

    public void setUniform4f(String name, float value1, float value2, float value3, float value4) {
        glUniform4f(uniformVariables.get(name).location, value1, value2, value3, value4);
    }

    public void setUniform4f(int location, float value1, float value2, float value3, float value4) {
        glUniform4f(location, value1, value2, value3, value4);
    }

    public void setUniform4f(String name, Vector4f value) {
        glUniform4f(uniformVariables.get(name).location, value.x, value.y, value.z, value.w);
    }

    public void setUniform4f(int location, Vector4f value) {
        glUniform4f(location, value.x, value.y, value.z, value.w);
    }

    public void setUniform4f(String name, Color value) {
        glUniform4f(uniformVariables.get(name).location, value.r, value.g, value.b, value.a);
    }

    public void setUniform4f(int location, Color value) {
        glUniform4f(location, value.r, value.g, value.b, value.a);
    }

    public void setUniform1fv(String name, float[] values) {
        glUniform1fv(uniformVariables.get(name).location, values);
    }

    public void setUniform1fv(int location, float[] values) {
        glUniform1fv(location, values);
    }

    public void setUniform2fv(String name, float[] values) {
        glUniform2fv(uniformVariables.get(name).location, values);
    }

    public void setUniform2fv(int location, float[] values) {
        glUniform2fv(location, values);
    }

    public void setUniform3fv(String name, float[] values) {
        glUniform3fv(uniformVariables.get(name).location, values);
    }

    public void setUniform3fv(int location, float[] values) {
        glUniform3fv(location, values);
    }

    public void setUniform4fv(String name, float[] values) {
        glUniform4fv(uniformVariables.get(name).location, values);
    }

    public void setUniform4fv(int location, float[] values) {
        glUniform4fv(location, values);
    }

    public void setUniformMatrix4fv(String name, Matrix4f matrix) {
        setUniformMatrix4fv(uniformVariables.get(name).location, matrix, false);
    }

    public void setUniformMatrix4fv(int location, Matrix4f matrix) {
        setUniformMatrix4fv(location, matrix, false);
    }

    public void setUniformMatrix4fv(String name, Matrix4f matrix, boolean transpose) {
        setUniformMatrix4fv(uniformVariables.get(name).location, matrix, transpose);
    }

    public void setUniformMatrix4fv(int location, Matrix4f matrix, boolean transpose) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = matrix.get(stack.mallocFloat(16));
            glUniformMatrix4fv(location, transpose, fb);
        }
    }

    public void setUniformMatrix3fv(String name, Matrix3f matrix) {
        setUniformMatrix3fv(uniformVariables.get(name).location, matrix, false);
    }

    public void setUniformMatrix3fv(int location, Matrix3f matrix) {
        setUniformMatrix3fv(location, matrix, false);
    }

    public void setUniformMatrix3fv(String name, Matrix3f matrix, boolean transpose) {
        setUniformMatrix3fv(uniformVariables.get(name).location, matrix, transpose);
    }

    public void setUniformMatrix3fv(int location, Matrix3f matrix, boolean transpose) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = matrix.get(stack.mallocFloat(9));
            glUniformMatrix3fv(location, transpose, fb);
        }
    }

    public void setUniformMatrix2fv(String name, Matrix2f matrix) {
        setUniformMatrix2fv(uniformVariables.get(name).location, matrix, false);
    }

    public void setUniformMatrix2fv(int location, Matrix2f matrix) {
        setUniformMatrix2fv(location, matrix, false);
    }

    public void setUniformMatrix2fv(String name, Matrix2f matrix, boolean transpose) {
        setUniformMatrix2fv(uniformVariables.get(name).location, matrix, transpose);
    }

    public void setUniformMatrix2fv(int location, Matrix2f matrix, boolean transpose) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            FloatBuffer fb = matrix.get(stack.mallocFloat(4));
            glUniformMatrix2fv(location, transpose, fb);
        }
    }

    private void compileProgram(String vertexShader, String fragmentShader) {
        vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);
        programHandle = createProgram();
        linkProgram();
        vertexAttributes = fetchAttributes();
        uniformVariables = fetchUniforms();
    }

    private int compileShader(int type, String code) {
        int handle = glCreateShader(type);

        if (handle == 0) {
            String errorMessage = "Could not create shader!";
            Out.printlnError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        glShaderSource(handle, code);
        glCompileShader(handle);

        int compilationStatus = glGetShaderi(handle, GL_COMPILE_STATUS);

        if (compilationStatus == 0) {
            int errorLength = glGetShaderi(handle, GL_INFO_LOG_LENGTH);
            String error = glGetShaderInfoLog(handle, errorLength);
            String errorMessage = "Could not compile shader: " + error + "!";
            Out.printlnError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        return handle;
    }

    private int createProgram() {
        int handle = glCreateProgram();

        if (handle == 0) {
            String errorMessage = "Could not create program!";
            Out.printlnError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }

        return handle;
    }

    private void linkProgram() {
        glAttachShader(programHandle, vertexShaderHandle);
        glAttachShader(programHandle, fragmentShaderHandle);
        glLinkProgram(programHandle);

        int linkingStatus = glGetProgrami(programHandle, GL_LINK_STATUS);

        if (linkingStatus == 0) {
            String errorMessage = "Could not link program!";
            int len = glGetProgrami(programHandle, GL_INFO_LOG_LENGTH);
            String err = glGetProgramInfoLog(programHandle, len);
            Out.printlnError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
    }

    private Map<String, VertexVariable> fetchAttributes() {
        Map<String, VertexVariable> vertexAttributes = new HashMap<>();
        int numberOfAttributes = glGetProgrami(programHandle, GL_ACTIVE_ATTRIBUTES);

        for (int i = 0; i < numberOfAttributes; i++) {
            IntBuffer sizeBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer typeBuffer = BufferUtils.createIntBuffer(1);

            String name = glGetActiveAttrib(programHandle, i, sizeBuffer, typeBuffer);
            int type = typeBuffer.get(0);
            int size = sizeBuffer.get(0);
            int location = glGetAttribLocation(programHandle, name);

            vertexAttributes.put(name, new VertexVariable(type, size, location));
        }

        return vertexAttributes;
    }

    private Map<String, VertexVariable> fetchUniforms() {
        Map<String, VertexVariable> uniformVariables = new HashMap<>();
        int numberOfAttributes = glGetProgrami(programHandle, GL_ACTIVE_UNIFORMS);

        for (int i = 0; i < numberOfAttributes; i++) {
            IntBuffer sizeBuffer = BufferUtils.createIntBuffer(1);
            IntBuffer typeBuffer = BufferUtils.createIntBuffer(1);

            String name = glGetActiveUniform(programHandle, i, sizeBuffer, typeBuffer);
            int type = typeBuffer.get(0);
            int size = sizeBuffer.get(0);
            int location = glGetUniformLocation(programHandle, name);

            uniformVariables.put(name, new VertexVariable(type, size, location));
        }

        return uniformVariables;
    }

    private void destroyShader(int handle) {
        if (programHandle != 0 && handle != 0) {
            glDetachShader(programHandle, handle);
            glDeleteShader(handle);
        }
    }

    private void destroyProgram() {
        destroyShader(vertexShaderHandle);
        destroyShader(fragmentShaderHandle);
        glDeleteProgram(programHandle);
    }

    // TODO

    @Override
    public String toString() {
        return "Shader{" +
                "programHandle=" + programHandle +
                ", vertexShaderHandle=" + vertexShaderHandle +
                ", fragmentShaderHandle=" + fragmentShaderHandle +
                ", vertexAttributes=" + vertexAttributes +
                ", uniformVariables=" + uniformVariables +
                '}';
    }
}
