package com.ustudents.engine.graphic.imgui.console;

import java.lang.reflect.Method;

public class ConsoleCommandData {
    public String name;
    public String description;
    public Method method;

    public ConsoleCommandData(String name, String description, Method method) {
        this.name = name;
        this.description = description;
        this.method = method;
    }
}
