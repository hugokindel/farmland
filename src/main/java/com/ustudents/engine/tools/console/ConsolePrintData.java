package com.ustudents.engine.tools.console;

import com.ustudents.engine.core.Resources;
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
                return Resources.getLocalizedText("consoleInfo") + " " + text;
            case Warning:
                return Resources.getLocalizedText("consoleWar") + " " + text;
            case Error:
                return Resources.getLocalizedText("consoleErr") + " " + text;
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
