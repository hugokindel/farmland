package com.ustudents.engine.core.console;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImBoolean;
import imgui.type.ImString;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Console {
    private ConsoleCommands consoleCommands;

    private static final ImBoolean showConsole = new ImBoolean(false);
    private static final ImBoolean autoScroll = new ImBoolean(true);
    private static final ImString userText = new ImString();
    private static final List<ConsolePrintData> inputs = new ArrayList<>();
    private static final List<ConsoleCommandData> listOfCommands = new ArrayList<>();
    private static boolean forceFocus = false;

    private static Console instance;

    public Console(ConsoleCommands consoleCommands) {
        this.consoleCommands = consoleCommands;
    }

    public static void create(ConsoleCommands consoleCommands) {
        instance = new Console(consoleCommands);
    }

    // In part from: https://github.com/ocornut/imgui/blob/master/imgui_demo.cpp
    public static void renderImGui() {
        if (showConsole.get()) {
            ImGuiUtils.setNextWindowWithSizeCentered(400, 400, ImGuiCond.Appearing);

            ImGui.begin("Console", showConsole);

            if (ImGui.beginPopupContextItem()) {
                if (ImGui.menuItem("Close")) {
                    showConsole.set(false);
                }

                ImGui.endPopup();
            }

            ImGui.textWrapped("Enter `help` for more informations.");

            if (ImGui.smallButton("Clear")) {
                clear();
            }

            ImGui.sameLine();

            if (ImGui.beginPopup("Options")) {
                ImGui.checkbox("Auto-scroll", autoScroll);
                ImGui.endPopup();
            }

            if (ImGui.smallButton("Options")) {
                ImGui.openPopup("Options");
            }

            ImGui.separator();

            float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();
            ImGui.beginChild("ScrollingRegion", 0, -footerHeightToReserve, false, ImGuiWindowFlags.HorizontalScrollbar);

            if (ImGui.beginPopupContextWindow()) {
                if (ImGui.selectable("Clear")) {
                    clear();
                }

                ImGui.endPopup();
            }

            ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 4, 1);

            for (ConsolePrintData data : inputs) {
                Color color = data.getColor();

                if (color != Color.WHITE) {
                    ImGui.pushStyleColor(ImGuiCol.Text, color.r, color.g, color.b, color.a);
                }

                ImGui.textUnformatted(data.getText());

                if (color != Color.WHITE) {
                    ImGui.popStyleColor();
                }
            }

            ImGui.popStyleVar();
            ImGui.endChild();

            ImGui.separator();

            boolean reclaimFocus = false;

            if (forceFocus) {
                reclaimFocus = true;
                forceFocus = false;
            }

            if (ImGui.inputText("Input", userText, ImGuiInputTextFlags.EnterReturnsTrue)) {
                if (!userText.get().isEmpty()) {
                    Out.printlnToFile("$> " + userText.get());
                    println("$> " + userText.get());

                    getListOfCommands();

                    if (listOfCommands.stream().anyMatch(command -> command.name.equals(userText.get()))) {
                        try {
                            listOfCommands.stream().filter(command -> command.name.equals(userText.get())).findFirst().get().method.invoke(get().consoleCommands);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        println(new ConsolePrintData("this is not a known command!", ConsolePrintData.Type.Error));
                    }

                    userText.set("");
                    reclaimFocus = true;
                }
            }

            ImGui.setItemDefaultFocus();

            if (reclaimFocus) {
                ImGui.setKeyboardFocusHere(-1);
            }

            ImGui.end();
        }
    }

    public static boolean exists() {
        return instance != null;
    }

    public static Console get() {
        return instance;
    }

    public static void show() {
        showConsole.set(!showConsole.get());
        userText.set("");
        if (showConsole.get()) {
            forceFocus = true;
        }
    }

    public static boolean visible() {
        return showConsole.get();
    }

    public static void clear() {
        inputs.clear();
    }

    public static void println(String text) {
        inputs.add(new ConsolePrintData(text));
    }

    public static void printlnWarning(String text) {
        inputs.add(new ConsolePrintData(text, ConsolePrintData.Type.Warning));
    }

    public static void printlnInfo(String text) {
        inputs.add(new ConsolePrintData(text, ConsolePrintData.Type.Info));
    }

    public static void printlnError(String text) {
        inputs.add(new ConsolePrintData(text, ConsolePrintData.Type.Error));
    }

    public static void println(ConsolePrintData data) {
        inputs.add(data);
    }

    public static List<ConsoleCommandData> getListOfCommands() {
        if (listOfCommands.isEmpty()) {
            for (Method method : findCommandMethods(get().consoleCommands.getClass())) {
                try {
                    String name = method.getAnnotation(ConsoleCommand.class).name().isEmpty() ?
                            method.getName() : method.getAnnotation(ConsoleCommand.class).name();
                    String description = method.getAnnotation(ConsoleCommand.class).description();

                    listOfCommands.add(new ConsoleCommandData(name, description, method));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return listOfCommands;
    }

    private static <T> List<Method> findCommandMethods(Class<T> type) {
        ArrayList<Method> methods = new ArrayList<>();

        Class<?> currentType = type;
        while (currentType != null) {
            for (Method method : currentType.getDeclaredMethods()) {
                if (method.isAnnotationPresent(ConsoleCommand.class)) {
                    methods.add(method);
                }
            }

            currentType = currentType.getSuperclass();
        }

        return methods;
    }
}
