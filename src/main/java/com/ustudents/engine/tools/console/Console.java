package com.ustudents.engine.tools.console;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
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

import static com.ustudents.engine.core.cli.option.Runnable.calculateLevenshteinDistance;

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
            ImGuiUtils.setNextWindowWithSizeCentered(700, 400, ImGuiCond.Appearing);

            ImGui.begin(Resources.getLocalizedText("console"), showConsole);

            if (ImGui.beginPopupContextItem()) {
                if (ImGui.menuItem(Resources.getLocalizedText("consoleClose"))) {
                    showConsole.set(false);
                }

                ImGui.endPopup();
            }

            ImGui.textWrapped(Resources.getLocalizedText("consoleEntHel"));

            if (ImGui.smallButton(Resources.getLocalizedText("consoleClear"))) {
                clear();
            }

            ImGui.sameLine();

            if (ImGui.beginPopup(Resources.getLocalizedText("consoleOptions"))) {
                ImGui.checkbox(Resources.getLocalizedText("consoleAutoScroll"), autoScroll);
                ImGui.endPopup();
            }

            if (ImGui.smallButton(Resources.getLocalizedText("consoleOptions"))) {
                ImGui.openPopup(Resources.getLocalizedText("consoleOptions"));
            }

            ImGui.separator();

            float footerHeightToReserve = ImGui.getStyle().getItemSpacingY() + ImGui.getFrameHeightWithSpacing();
            ImGui.beginChild("ScrollingRegion", 0, -footerHeightToReserve, false, ImGuiWindowFlags.HorizontalScrollbar);

            if (ImGui.beginPopupContextWindow()) {
                if (ImGui.selectable(Resources.getLocalizedText("consoleClear"))) {
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
                    String[] argDesc = method.getAnnotation(ConsoleCommand.class).argsDescription();

                    listOfCommands.add(new ConsoleCommandData(name, description, method, authority, argDesc));
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

            String[] split = commandName.split(" ");
            String command = split[0];
            List<String> args = new ArrayList<>(Arrays.asList(split).subList(1, split.length));
            List<String> argsCopy = new ArrayList<>(args);

            boolean inString = false;
            int stringPos = -1;
            int numSupp = 0;

            for (int i = 0; i < argsCopy.size(); i++) {
                String arg = argsCopy.get(i);

                if (inString) {
                    args.set(stringPos, args.get(stringPos) + " " + arg);

                    if (arg.endsWith("\"")) {
                        args.set(stringPos, args.get(stringPos).substring(0, args.get(stringPos).length() - 1));
                        inString = false;
                        stringPos = -1;
                    }

                    args.remove(i - numSupp);
                    numSupp += 1;
                } else if (arg.startsWith("\"")) {
                    inString = true;
                    stringPos = i;

                    if (arg.endsWith("\"")) {
                        args.set(stringPos, arg.substring(1, arg.length() - 1));

                        inString = false;
                        stringPos = -1;
                    } else {
                        args.set(stringPos, arg.substring(1));
                    }
                }
            }

            if (listOfCommands.stream().anyMatch(c -> c.name.equals(command) && (c.method.getParameters().length == args.size() || c.method.isVarArgs()))) {
                for (ConsoleCommandData c : listOfCommands) {
                    if (c.name.equals(command) && (c.method.getParameters().length == args.size() || c.method.isVarArgs())) {
                        if (Arrays.stream(c.authority).anyMatch(e -> e == Game.get().getNetMode())) {
                            if (args.isEmpty()) {
                                c.method.invoke(get().consoleCommands);
                            } else {
                                if (c.method.isVarArgs()) {
                                    c.method.invoke(get().consoleCommands, new Object[] { args.toArray() });
                                } else {
                                    List<Object> realList = new ArrayList<>();

                                    for (int i = 0; i < args.size(); i++) {
                                        if (c.method.getParameters()[i].getType() == Integer.class) {
                                            realList.add(Integer.parseInt(args.get(i)));
                                        } else {
                                            realList.add(args.get(i));
                                        }
                                    }

                                    c.method.invoke(get().consoleCommands, realList.toArray());
                                }
                            }
                        } else {
                            printlnError(Resources.getLocalizedText("consoleNoAuthority"));
                        }
                    }
                }
            } else {
                printlnError(Resources.getLocalizedText("consoleUnknown"));

                String nearest = findNearestCommand(command, listOfCommands);

                if (!nearest.isEmpty()) {
                    printlnError(Resources.getLocalizedText("consoleMean", findNearestCommand(command, listOfCommands)));
                }
            }
        } catch (Exception e) {
            if (e.getMessage().toLowerCase().contains("wrong number of arguments")) {
                printlnError(Resources.getLocalizedText("consoleWrongNumArg"));
            } else {
                printlnError(Resources.getLocalizedText("consoleError"));
            }
        }
    }

    private static String findNearestCommand(String unknownOption, List<ConsoleCommandData> commands) {
        int distance = -1;
        String nearest = "";

        for (ConsoleCommandData command : commands) {
            int optionDistance = calculateLevenshteinDistance(unknownOption, command.name);

            if (distance == -1 || optionDistance < distance) {
                distance = optionDistance;
                nearest = command.name;
            }
        }

        if (!nearest.isEmpty()) {
            return nearest;
        }

        return "";
    }
}
