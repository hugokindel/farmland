package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.network.NetMode;

import java.lang.reflect.Method;

public class ConsoleCommandData {
    public String name;
    public String description;
    public Method method;
    public NetMode[] authority;

    public ConsoleCommandData(String name, String description, Method method, NetMode[] authority) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.authority = authority;
    }
}
