package com.ustudents.engine.graphic.imgui.console;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.network.NetMode;
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
    private static boolean scrollToBottom = false;

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

            if (scrollToBottom || (autoScroll.get() && ImGui.getScrollY() >= ImGui.getScrollMaxY())) {
                ImGui.setScrollHereY(1.0f);
                scrollToBottom = false;
            }

            ImGui.popStyleVar();
            ImGui.endChild();

            ImGui.separator();

            boolean reclaimFocus = false;

            if (forceFocus) {
                reclaimFocus = true;
                forceFocus = false;
            }

            ImGui.pushItemWidth(ImGui.getWindowWidth() - 30);

            if (ImGui.inputText("", userText, ImGuiInputTextFlags.EnterReturnsTrue)) {
                if (!userText.get().isEmpty()) {
                    println("$> " + userText.get());
                    tryExecuteCommand(userText.get());
                    userText.set("");
                    reclaimFocus = true;
                    scrollToBottom = true;
                }
            }

            ImGui.popItemWidth();

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

    public static void println(ConsolePrintData data) {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Out.println(data.getText());
        } else {
            inputs.add(data);
            Out.printlnToFile(data);
        }
    }

    public static void println(String text) {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Out.println(text);
        } else {
            ConsolePrintData data = new ConsolePrintData(text);
            inputs.add(data);
            Out.printlnToFile(data.getText());
        }
    }

    public static void printlnInfo(String text) {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Out.printlnInfo(text);
        } else {
            ConsolePrintData data = new ConsolePrintData(text, ConsolePrintData.Type.Info);
            inputs.add(data);
            Out.printlnToFile(data.getText());
        }
    }

    public static void printlnWarning(String text) {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Out.printlnWarning(text);
        } else {
            ConsolePrintData data = new ConsolePrintData(text, ConsolePrintData.Type.Warning);
            inputs.add(data);
            Out.printlnToFile(data.getText());
        }
    }

    public static void printlnError(String text) {
        if (Game.get().getNetMode() == NetMode.DedicatedServer) {
            Out.printlnError(text);
        } else {
            ConsolePrintData data = new ConsolePrintData(text, ConsolePrintData.Type.Error);
            inputs.add(data);
            Out.printlnToFile(data.getText());
        }
    }

    public static List<ConsoleCommandData> getListOfCommands() {
        if (listOfCommands.isEmpty()) {
            for (Method method : findCommandMethods(get().consoleCommands.getClass())) {
                try {
                    String name = method.getAnnotation(ConsoleCommand.class).name().isEmpty() ?
                            method.getName() : method.getAnnotation(ConsoleCommand.class).name();
                    String description = method.getAnnotation(ConsoleCommand.class).description();
                    NetMode[] authority = method.getAnnotation(ConsoleCommand.class).authority();

                    listOfCommands.add(new ConsoleCommandData(name, description, method, authority));
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

    public static void tryExecuteCommand(String commandName) {
        try {
            getListOfCommands();

            if (listOfCommands.stream().anyMatch(command -> command.name.equals(commandName))) {
                for (ConsoleCommandData command : listOfCommands) {
                    if (command.name.equals(commandName)) {
                        if (Arrays.stream(command.authority).anyMatch(e -> e == Game.get().getNetMode())) {
                            command.method.invoke(get().consoleCommands);
                        } else {
                            printlnError("You do not have authority to run this command!");
                        }
                    }
                }
            } else {
                printlnError("This is not a known command!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
