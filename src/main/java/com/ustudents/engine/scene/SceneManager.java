package com.ustudents.engine.scene;

import com.ustudents.engine.utility.TypeUtil;
import com.ustudents.engine.graphics.imgui.Debugger;

import java.util.ArrayList;
import java.util.List;

/** The scene manager which handles the list of scenes and interact with them. */
@SuppressWarnings({"unchecked"})
public class SceneManager {
    /** The list of scenes. */
    List<Scene> scenes;

    /** The current scene ID. */
    int currentSceneIndex;

    boolean transitioningScene;

    Debugger debugTools;

    /** Class constructor. */
    public SceneManager() {
        scenes = new ArrayList<>();
        currentSceneIndex = 0;
        transitioningScene = false;
        debugTools = new Debugger();
    }

    public <T extends Scene> void initialize(Class<T> classType, Object... args) {
        debugTools.initialize(this);

        if (args.length == 0) {
            changeScene(classType);
        } else {
            changeScene(classType, args);
        }
    }

    /**
     * Creates a scene of the given type.
     *
     * @param classType The scene type class.
     * @param args The scene type constructor arguments.
     * @param <T> The scene type.
     *
     * @return the scene.
     */
    public <T extends Scene> T changeScene(Class<T> classType, Object... args) {
        if (args.length == 0) {
            scenes.add(TypeUtil.createInstance(classType));
        } else {
            scenes.add(TypeUtil.createInstance(classType, args));
        }

        int index = scenes.size() - 1;

        transitioningScene = true;
        scenes.get(index).create(this);

        return (T)scenes.get(index);
    }

    /** Updates the scene. */
    public void update(double dt) {
        if (scenes.size() > currentSceneIndex) {
            debugTools.update(dt);
            getScene().update(dt);
        }
    }

    /** Renders the scene. */
    public void render() {
        if (scenes.size() > currentSceneIndex) {
            getScene().render();
        }
    }

    public void renderImGui() {
        if (scenes.size() > currentSceneIndex) {
            debugTools.renderImGui();
            getScene().renderImGui();
        }
    }

    /** Destroys the scene. */
    public void destroy() {
        if (scenes.size() > currentSceneIndex) {
            getScene().destroy();
        }
    }

    public void startFrame() {
        if (transitioningScene) {
            currentSceneIndex = scenes.size() - 1;
            scenes.get(currentSceneIndex).initialize();
            transitioningScene = false;
        }
    }

    /** Updates the registry of the scene. */
    public void endFrame() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getRegistry().update();
        }
    }

    /** Gets the scene. */
    public Scene getScene() {
        return scenes.get(currentSceneIndex);
    }
}
