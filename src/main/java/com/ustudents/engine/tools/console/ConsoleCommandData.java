package com.ustudents.engine.tools.console;

import com.ustudents.engine.network.NetMode;

import java.lang.reflect.Method;

public class ConsoleCommandData {
    public String name;
    public String description;
    public Method method;
    public NetMode[] authority;
    public String[] argDesc;

    public ConsoleCommandData(String name, String description, Method method, NetMode[] authority, String[] argDesc) {
        this.name = name;
        this.description = description;
        this.method = method;
        this.authority = authority;
        this.argDesc = argDesc;
    }
}
