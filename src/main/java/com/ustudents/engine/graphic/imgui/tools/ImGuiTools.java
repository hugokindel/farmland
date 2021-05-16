package com.ustudents.engine.graphic.imgui.tools;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.SoundSource;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import com.ustudents.engine.core.cli.option.Runnable;
import com.ustudents.engine.scene.SceneManager;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.graphic.Camera;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiTreeNodeFlags;
import imgui.type.*;
import org.joml.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class ImGuiTools {
    private final ImBoolean showDemoWindow = new ImBoolean(false);
    private final ImBoolean showSceneHierarchyWindow = new ImBoolean(true);
    private final ImBoolean showInspectorWindow = new ImBoolean(true);
    private final ImBoolean showSettingsWindow = new ImBoolean(false);
    private final ImBoolean showPerformanceCounter = new ImBoolean(false);
    private static boolean vsyncCurrentState;
    private static ImBoolean useVsync;
    private int selectedInspectorEntity = -1;
    SceneManager sceneManager;
    //private final Map<Integer, Map<Integer, Map<String, Object>>> values = new HashMap<>();

    public void initialize(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        vsyncCurrentState = Game.get().getVsync();
        useVsync = new ImBoolean(Game.get().getVsync());
    }

    public void update(float dt) {
        if (useVsync != null && vsyncCurrentState != useVsync.get()) {
            vsyncCurrentState = useVsync.get();
            Game.get().setVsync(vsyncCurrentState);
        }
    }

    public void renderImGui() {
        drawMainMenu();
        if (showSceneHierarchyWindow.get()) {
            drawSceneHierarchy();
        }
        if (showInspectorWindow.get()) {
            drawInspector();
        }
        if (showSettingsWindow.get()) {
            drawSettings();
        }
        if (showDemoWindow.get()) {
            drawDemo();
        }
        if (showPerformanceCounter.get()) {
            drawPerformanceCounter();
        }
    }

    private void drawMainMenu() {
        if (ImGui.beginMainMenuBar()) {
            if (ImGui.beginMenu("File")) {
                if (ImGui.menuItem("Quit")) {
                    Game.get().quit();
                }

                ImGui.endMenu();
            }

            if(ImGui.beginMenu("View")) {
                ImGui.menuItem("Show scene hierarchy", "", showSceneHierarchyWindow);
                ImGui.menuItem("Show inspector", "", showInspectorWindow);
                ImGui.separator();
                ImGui.menuItem("Show settings", "", showSettingsWindow);
                ImGui.menuItem("Show performance counter", "", showPerformanceCounter);

                ImGui.endMenu();
            }

            /*if (ImGui.beginMenu("Help")) {
                ImGui.menuItem("Show ImGui demo", "", showDemoWindow);

                ImGui.endMenu();
            }*/

            ImGui.endMainMenuBar();
        }
    }

    private void drawSceneHierarchy() {
        ImGui.setNextWindowSize(250, 600, ImGuiCond.Appearing);
        ImGuiUtils.setNextWindowPos(0, 0, ImGuiCond.Appearing);

        ImGui.begin("Scene hierarchy", showSceneHierarchyWindow);

        ImGui.text(sceneManager.getCurrentScene().getClass().getSimpleName());

        ImGui.separator();

        int flags = ImGuiTreeNodeFlags.Leaf;
        if (selectedInspectorEntity == -2) {
            flags |= ImGuiTreeNodeFlags.Selected;
        }
        if (ImGui.treeNodeEx("camera", flags)) {
            if (ImGui.isItemClicked()) {
                selectedInspectorEntity = -2;
            }

            ImGui.treePop();
        }

        drawEntitiesTree(sceneManager.getCurrentScene().getRegistry().getEntitiesAtRoot(), true);

        ImGui.end();
    }

    private void drawEntitiesTree(List<Entity> entities, boolean root) {
        if (!root || entities.size() > 0) {
            for (Entity child : entities) {
                int flags = 0;
                if (child.getChildren().size() == 0) {
                    flags |= ImGuiTreeNodeFlags.Leaf;
                }
                if (selectedInspectorEntity == child.getId()) {
                    flags |= ImGuiTreeNodeFlags.Selected;
                }

                if (ImGui.treeNodeEx(child.getId(), flags, child.getNameOrIdentifier())) {
                    if (ImGui.isItemClicked()) {
                        selectedInspectorEntity = child.getId();
                    }

                    drawEntitiesTree(child.getChildren(), false);

                    ImGui.treePop();
                }
            }
        } else {
            ImGui.text("This scene has no entity");
        }
    }

    private void drawInspector() {
        ImGui.setNextWindowSize(375, 600, ImGuiCond.Appearing);
        ImGuiUtils.setNextWindowPosFromEnd(-375, 0, ImGuiCond.Appearing);

        ImGui.begin("Inspector", showInspectorWindow);

        if (selectedInspectorEntity == -2) {
            ImGui.text("camera");

            ImGui.separator();
            Camera camera = sceneManager.getCurrentScene().getWorldCamera();
            Vector2f position = camera.getPosition();

            if (ImGui.treeNode("transform")) {
                ImGuiUtils.setEnabled(false);
                drawField("position", Vector2f.class, new float[] {position.x, position.y});
                drawField("rotation", Float.class, new ImFloat(camera.getRotation()));
                drawField("zoom", Float.class, new ImFloat(camera.getZoom()));
                ImGuiUtils.setEnabled(true);

                ImGui.treePop();
            }

            if (ImGui.treeNode("limits")) {
                ImGuiUtils.setEnabled(false);
                drawField("minimalZoom", Float.class, new ImFloat(camera.getMinimalZoom()));
                drawField("maximalZoom", Float.class, new ImFloat(camera.getMaximalZoom()));
                ImGuiUtils.setEnabled(true);

                ImGui.treePop();
            }

            if (ImGui.treeNode("advanced")) {
                ImGuiUtils.setEnabled(false);
                drawField("view", Matrix3x2f.class, camera.getViewAsArray());
                drawField("viewProjection", Matrix4f.class, camera.getViewProjectionAsArray());
                ImGuiUtils.setEnabled(true);

                ImGui.treePop();
            }

        } else if (selectedInspectorEntity != -1) {
            Entity entity = sceneManager.getCurrentScene().getRegistry().getEntityById(selectedInspectorEntity);

            if (entity != null) {
                int entityId = entity.getId();
                List<Component> components = new ArrayList<>(entity.getComponents());
                components.sort(Comparator.comparingInt(Component::getId));

                ImGui.text(entity.getNameOrIdentifier());

                ImGui.separator();

                if (components.size() > 0) {
                /*if (!values.containsKey(entityId)) {
                    values.put(entityId, new HashMap<>());
                }*/

                    for (Component component : components) {
                        if (ImGui.treeNode(component.getClass().getSimpleName())) {
                            drawEditableFields(entityId, component);
                            ImGui.treePop();
                        }

                        ImGui.separator();
                    }
                } else {
                    ImGui.text("This entity has no components");
                }
            } else {
                selectedInspectorEntity = -1;
            }

        } else {
            ImGui.text("No entity selected");
        }

        ImGui.end();
    }

    private void drawEditableFields(int entityId, Object component) {
        ArrayList<Field> fields = new ArrayList<>(Arrays.asList(Runnable.class.getDeclaredFields()));
        fields.addAll(Arrays.asList(component.getClass().getDeclaredFields()));
        fields.removeIf(field -> !field.isAnnotationPresent(Viewable.class));

        /*if (!values.get(entityId).containsKey(componentId)) {
            values.get(entityId).put(componentId, new HashMap<>());

            for (Field field : fields) {
                try {
                    Class<?> type = field.getType();
                    String name = field.getName();
                    Object originalValue = field.get(component);
                    Map<String, Object> container = values.get(entityId).get(componentId);

                    if (type == Vector2f.class) {
                        Vector2f value = (Vector2f)originalValue;
                        float[] floatArray = {value.x, value.y};
                        container.put(name, floatArray);
                    } else if (type == Float.class) {
                        Float value = (Float)originalValue;
                        ImFloat imFloatValue = new ImFloat(value);
                        container.put(name, imFloatValue);
                    } else if (type == Integer.class) {
                        Integer value = (Integer)originalValue;
                        ImInt imIntValue = new ImInt(value);
                        container.put(name, imIntValue);
                    } else if (type == String.class) {
                        String value = (String)originalValue;
                        ImString imStringValue = new ImString(value);
                        container.put(name, imStringValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }*/

        try {
            for (Field field : fields) {
                field.setAccessible(true);

                String name = field.getName();
                Class<?> type = field.getType();
                //Object value = values.get(entityId).get(componentId).get(field.getName());
                Object value = null;

                if (type == Vector2f.class) {
                    Vector2f originalValue = (Vector2f)field.get(component);
                    value = new float[]{originalValue.x, originalValue.y};
                } else if (type == Vector4f.class) {
                    Vector4f originalValue = (Vector4f)field.get(component);
                    value = new float[]{originalValue.x, originalValue.y, originalValue.z, originalValue.w};
                } else if (type == Float.class) {
                    Float originalValue = (Float)field.get(component);
                    value = new ImFloat(originalValue);
                } else if (type == Integer.class) {
                    Integer originalValue = (Integer)field.get(component);
                    value = new ImInt(originalValue);
                } else if (type == String.class) {
                    String originalValue = (String)field.get(component);
                    value = new ImString(originalValue);
                } else if (type == Vector4i.class) {
                    Vector4i originalValue = (Vector4i)field.get(component);
                    value = new int[]{originalValue.x, originalValue.y, originalValue.z, originalValue.w};
                } else if (type == Font.class || type == Texture.class || type == Color.class || type == SoundSource.class) {
                    value = field.get(component);
                } else if (type.isAnnotationPresent(Viewable.class)) {
                    if (ImGui.treeNode(name)) {
                        drawEditableFields(entityId, field.get(component));
                        ImGui.treePop();
                    }
                }

                drawField(name, type, value);
            }

            if (fields.isEmpty()) {
                ImGui.text("no values to debug");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (component instanceof Component) {
            ((Component)component).renderImGui();
        }
    }

    private void drawSettings() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);

        ImGui.begin("Settings", showSettingsWindow);

        ImGui.checkbox("Enable V-sync", useVsync);

        ImGui.end();
    }

    private void drawPerformanceCounter() {
        ImGuiUtils.setNextWindowWithSizeCentered(200, 100, ImGuiCond.Appearing);

        ImGui.begin("Performance counter", showPerformanceCounter);

        int fps = Game.get().getTimer().getFPS();
        double ms = BigDecimal.valueOf(Game.get().getTimer().getFrameDuration())
                .setScale(3, RoundingMode.HALF_UP).doubleValue();

        ImGui.text("FPS: " + fps);
        ImGui.text("Framerate: " + ms + "/ms");
        ImGui.text("Number of entities: " + sceneManager.getCurrentScene().getRegistry().getTotalNumberOfEntities());

        ImGui.end();
    }

    private void drawDemo() {
        ImGui.showDemoWindow(showDemoWindow);
    }

    private void drawField(String name, Class<?> type, Object value) {
        if (type == Vector2f.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputFloat2(name, (float[])value);
            ImGuiUtils.setEnabled(true);
        } else if (type == Vector4i.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputInt4(name, (int[])value);
            ImGuiUtils.setEnabled(true);
        } else if (type == Vector4f.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputFloat4(name, (float[])value);
            ImGuiUtils.setEnabled(true);
        } else if (type == Float.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputFloat(name, (ImFloat)value);
            ImGuiUtils.setEnabled(true);
        } else if (type == Integer.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputInt(name, (ImInt)value);
            ImGuiUtils.setEnabled(true);
        } else if (type == String.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.inputText(name, (ImString)value);
            ImGuiUtils.setEnabled(true);
        } else if (type == Matrix4f.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.text(name);
            ImGui.inputFloat4("m0", ((float[][])value)[0]);
            ImGui.inputFloat4("m1", ((float[][])value)[1]);
            ImGui.inputFloat4("m2", ((float[][])value)[2]);
            ImGui.inputFloat4("m3", ((float[][])value)[3]);
            ImGuiUtils.setEnabled(true);
        } else if (type == Matrix3x2f.class) {
            ImGuiUtils.setEnabled(false);
            ImGui.text(name);
            ImGui.inputFloat2("m0", ((float[][])value)[0]);
            ImGui.inputFloat2("m1", ((float[][])value)[1]);
            ImGui.inputFloat2("m2", ((float[][])value)[2]);
            ImGuiUtils.setEnabled(true);
        } else if (type == Texture.class) {
            if (ImGui.treeNode(name)) {
                ImGuiUtils.setEnabled(false);
                ImGui.inputText("path", new ImString(((Texture)value).getPath()));
                ImGui.inputInt("handle", new ImInt(((Texture)value).getHandle()));
                ImGui.inputInt("width", new ImInt(((Texture)value).getWidth()));
                ImGui.inputInt("height", new ImInt(((Texture)value).getHeight()));
                ImGui.inputInt("numComps", new ImInt(((Texture)value).getNumberOfComponents()));
                ImGuiUtils.setEnabled(true);
                ImGui.treePop();
            }
        } else if (type == Font.class) {
            if (ImGui.treeNode(name)) {
                drawField("texture", Texture.class, ((Font)value).getTexture());
                ImGuiUtils.setEnabled(false);
                ImGui.inputText("path", new ImString(((Font)value).getPath()));
                ImGui.inputInt("fontSize", new ImInt(((Font)value).getSize()));
                ImGui.inputInt("kerning", new ImInt(((Font)value).getKerning()));
                ImGui.inputInt("ascent", new ImInt(((Font)value).getAscent()));
                ImGui.inputInt("descent", new ImInt(((Font)value).getDescent()));
                ImGui.inputInt("lineGap", new ImInt(((Font)value).getLineGap()));
                ImGuiUtils.setEnabled(true);
                ImGui.treePop();
            }
        } else if (type == Color.class) {
            Color color = (Color)value;
            if (ImGui.treeNode(name)) {
                ImGuiUtils.setEnabled(false);
                ImGui.colorPicker4(name, new float[] {color.r, color.g, color.b, color.a});
                ImGuiUtils.setEnabled(true);
                ImGui.treePop();
            }
        } else if (type == SoundSource.class) {
            SoundSource soundSource = (SoundSource)value;
            if (ImGui.treeNode(name)) {
                ImGuiUtils.setEnabled(false);
                ImGui.inputInt("handle", new ImInt(soundSource.getHandle()));
                ImGuiUtils.setEnabled(true);
                if (ImGui.treeNode("sound")) {
                    ImGuiUtils.setEnabled(false);
                    ImGui.inputText("path", new ImString(soundSource.getSound().getPath()));
                    ImGuiUtils.setEnabled(true);
                    ImGui.treePop();
                }
                ImGui.treePop();
            }
        }
    }
}
