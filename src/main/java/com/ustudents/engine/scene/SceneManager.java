package com.ustudents.engine.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.graphic.debug.DebugTools;
import com.ustudents.engine.utility.TypeUtil;
import com.ustudents.engine.graphic.imgui.ImGuiTools;

import java.util.ArrayList;
import java.util.List;

/** The scene manager which handles the list of scenes and interact with them. */
public class SceneManager {
    /** The list of scenes. */
    private final List<Scene> scenes = new ArrayList<>();

    /** The current scene ID. */
    private int currentSceneIndex = 0;

    /** Defines if we need to transition before the next frame. */
    private boolean transitioningScene = false;

    /**
     * Creates a scene of the given type.
     *
     * @param classType The scene type class.
     * @param args The scene type constructor arguments.
     * @param <T> The scene type.
     *
     * @return the scene.
     */
    public <T extends Scene> void changeScene(Class<T> classType, Object... args) {
        if (args.length == 0) {
            scenes.add(TypeUtil.createInstance(classType));
        } else {
            scenes.add(TypeUtil.createInstance(classType, args));
        }

        transitioningScene = true;
    }

    /** Updates the scene. */
    public void update(float dt) {
        if (scenes.size() > currentSceneIndex) {
            getScene().update(dt);
            getScene().getRegistry().update(dt);

            if (Game.get().isImGuiToolsEnabled()) {
                Game.get().getImGuiTools().update(dt);
            }
        }
    }

    /** Renders the scene. */
    public void render() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getRegistry().render();
            getScene().render();

            if (Game.get().isDebugToolsEnabled()) {
                Game.get().getDebugTools().render();
            }
        }
    }

    /** Renders the scene (for ImGui windows). */
    public void renderImGui() {
        if (scenes.size() > currentSceneIndex) {
            getScene().renderImGui();
            Game.get().getImGuiTools().renderImGui();
        }
    }

    /** Destroys the scene. */
    public void destroy() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getSpritebatch().destroy();
            getScene().destroy();

            if (Game.isDebugging()) {
                Out.printlnDebug("Scene destroyed.");
            }
        }
    }

    /** Called at the beginning of the frame (to check if we need to do a scene transition). */
    public void startFrame() {
        if (transitioningScene) {
            Game.get().getSoundManager().removeAll();

            currentSceneIndex = scenes.size() - 1;

            if (currentSceneIndex > 0) {
                scenes.get(currentSceneIndex - 1).getSpritebatch().destroy();
                scenes.get(currentSceneIndex - 1).destroy();

                if (Game.isDebugging()) {
                    Out.printlnDebug("Previous scene destroyed.");
                }
            }

            scenes.get(currentSceneIndex).create(this);
            scenes.get(currentSceneIndex)._initialize();

            transitioningScene = false;

            if (Game.isDebugging()) {
                Out.printlnDebug("New scene created.");
            }
        }
    }

    /** Updates the registry of the scene. */
    public void endFrame() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getRegistry().updateEntities();
        }
    }

    /** @return the current scene. */
    public Scene getScene() {
        return scenes.get(currentSceneIndex);
    }
}
