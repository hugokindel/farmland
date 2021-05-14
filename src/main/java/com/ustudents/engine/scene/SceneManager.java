package com.ustudents.engine.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.utility.TypeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/** The scene manager which handles the list of scenes and interact with them. */
public class SceneManager {
    /** The list of scenes. */
    private final List<Scene> scenes = new ArrayList<>();

    /** The current scene ID. */
    private int currentSceneIndex = 0;

    /** Defines if we need to transition before the next frame. */
    private boolean transitioningScene = false;

    private boolean addLastToTypeStack = true;

    /** The registry for every entities in the scene. */
    private final Registry registry = new Registry();

    private final Stack<Class<?>> lastScenesType = new Stack<>();

    /**
     * Changes the current scene to the given scene.
     *
     * @param scene The new current scene.
     */
    public void changeScene(Scene scene) {
        changeScene(scene, true);
    }

    public void changeScene(Scene scene, boolean addLastToTypeStack) {
        scenes.add(scene);
        transitioningScene = true;
        this.addLastToTypeStack = addLastToTypeStack;
    }

    /**
     * Changes the current scene to the given scene of specific type.
     *
     * @param classType The scene type class.
     * @param args The scene type constructor arguments.
     * @param <T> The scene type.
     */
    @Deprecated
    public <T extends Scene> void changeScene(Class<T> classType, Object... args) {
        if (args.length == 0) {
            changeScene(TypeUtil.createInstance(classType));
        } else {
            changeScene(TypeUtil.createInstance(classType, args));
        }
    }

    /** Updates the scene. */
    public void update(float dt) {
        if (scenes.size() > currentSceneIndex) {
            getCurrentScene().update(dt);
            getCurrentScene().getRegistry().update(dt);

            if (Game.get().isImGuiToolsEnabled()) {
                Game.get().getImGuiTools().update(dt);
            }
        }
    }

    /** Renders the scene. */
    public void render() {
        if (scenes.size() > currentSceneIndex) {
            getCurrentScene().getRegistry().render();
            getCurrentScene().render();

            if (Game.get().isDebugToolsEnabled()) {
                Game.get().getDebugTools().render();
            }
        }
    }

    /** Renders the scene (for ImGui windows). */
    public void renderImGui() {
        if (scenes.size() > currentSceneIndex) {
            getCurrentScene().renderImGui();
        }
    }

    /** Destroys the scene. */
    public void destroy() {
        if (scenes.size() > currentSceneIndex) {
            if (getCurrentScene().getSpritebatch() != null) {
                getCurrentScene().getSpritebatch().destroy();
            }

            getCurrentScene().destroy();

            if (Game.isDebugging()) {
                Out.printlnDebug("Scene destroyed.");
            }
        }
    }

    /** Called at the beginning of the frame (to check if we need to do a scene transition). */
    public void startFrame() {
        if (transitioningScene) {
            if (currentSceneIndex > 0) {
                if (scenes.get(currentSceneIndex).getSpritebatch() != null) {
                    scenes.get(currentSceneIndex).getSpritebatch().destroy();
                }

                scenes.get(currentSceneIndex).destroy();
            }

            currentSceneIndex = scenes.size() - 1;

            if (currentSceneIndex > 0) {
                if (addLastToTypeStack) {
                    lastScenesType.add(scenes.get(currentSceneIndex - 1).getClass());
                }

                registry.clearRegistry();
                registry.addAllKeptEntitiesToSystemCheck();

                addLastToTypeStack = true;

                if (Game.isDebugging()) {
                    Out.printlnDebug("Previous scene destroyed.");
                }
            }

            scenes.get(currentSceneIndex).create(this);
            scenes.get(currentSceneIndex).initializeInternals();
            scenes.get(currentSceneIndex).registry.updateEntities();
            Window.get().actualizeCursorType();

            for (Entity entity : registry.getEntities().values()) {
                for (Component component : entity.getComponents()) {
                    component.onSceneLoaded();
                }
            }

            Input.recalculateMousePosition();

            transitioningScene = false;

            if (Game.isDebugging()) {
                Out.printlnDebug("New scene created.");
            }
        }
    }

    /** Updates the registry of the scene. */
    public void endFrame() {
        if (scenes.size() > currentSceneIndex) {
            getCurrentScene().getRegistry().updateEntities();
        }
    }

    /** @return the scene manager. */
    public static SceneManager get() {
        return Game.get().getSceneManager();
    }

    /** @return the current scene. */
    public static Scene getScene() {
        return Game.get().getSceneManager().getCurrentScene();
    }

    /** @return the current scene. */
    public Scene getCurrentScene() {
        if (scenes.isEmpty()) {
            return null;
        }

        return scenes.get(currentSceneIndex);
    }

    public Registry getRegistry() {
        return registry;
    }

    public Class<?> popTypeOfLastScene() {
        return lastScenesType.pop();
    }

    public void goBack() {
        try {
            changeScene((Scene) lastScenesType.pop().getConstructors()[0].newInstance(), false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
