package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.graphic.Color;

public class ConsolePrintData {
    public enum Type {
        None,
        Info,
        Warning,
        Error
    }

    private String text;
    private Color color;
    private Type type;

    public ConsolePrintData(String text) {
        this(text, Type.None);
    }

    public ConsolePrintData(String text, Type type) {
        this(text, type, Color.WHITE);
    }

    public ConsolePrintData(String text, Type type, Color color) {
        this.text = text;
        this.type = type;
        this.color = color;
    }

    public String getText() {
        switch (type) {
            case None:
                return text;
            case Info:
                return "Info: " + text;
            case Warning:
                return "Warning: " + text;
            case Error:
                return "Error: " + text;
        }

        return text;
    }

    public Color getColor() {
        switch (type) {
            case None:
                return color;
            case Info:
                return Color.BLUE;
            case Warning:
                return Color.YELLOW;
            case Error:
                return Color.RED;
        }

        return color;
    }
}
