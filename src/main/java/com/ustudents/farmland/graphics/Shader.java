package com.ustudents.farmland.graphics;

import com.ustudents.farmland.cli.print.Out;
import static org.lwjgl.opengl.GL32.*;

public class Shader {
    private int programHandle;
    private int vertexShaderHandle;
    private int fragmentShaderHandle;

    public Shader(String vertexShader, String fragmentShader) {
        compileProgram(vertexShader, fragmentShader);
    }

    public void destroy() {
        destroyProgram();
    }

    private void compileProgram(String vertexShader, String fragmentShader) {
        vertexShaderHandle = compileShader(GL_VERTEX_SHADER, vertexShader);
        fragmentShaderHandle = compileShader(GL_FRAGMENT_SHADER, fragmentShader);
        programHandle = createProgram();
        linkProgram();
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

    public int getProgramHandle() {
        return programHandle;
    }

    public int getVertexShaderHandle() {
        return vertexShaderHandle;
    }

    public int getFragmentShaderHandle() {
        return fragmentShaderHandle;
    }

    @Override
    public String toString() {
        return "Shader{" +
                "programHandle=" + programHandle +
                ", vertexShaderHandle=" + vertexShaderHandle +
                ", fragmentShaderHandle=" + fragmentShaderHandle +
                '}';
    }
}
