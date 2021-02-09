package com.ustudents.engine.scene;

import com.ustudents.engine.utility.TypeUtil;
import com.ustudents.engine.graphic.imgui.Debugger;

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

    public void initialize() {
        debugTools.initialize(this);
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
    public <T extends Scene> void changeScene(Class<T> classType, Object... args) {
        if (args.length == 0) {
            scenes.add(TypeUtil.createInstance(classType));
        } else {
            scenes.add(TypeUtil.createInstance(classType, args));
        }

        int index = scenes.size() - 1;

        transitioningScene = true;
        scenes.get(index).create(this);
    }

    /** Updates the scene. */
    public void update(double dt) {
        if (scenes.size() > currentSceneIndex) {
            getScene().update(dt);
            getScene().getRegistry().update(dt);
            debugTools.update(dt);
        }
    }

    /** Renders the scene. */
    public void render() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getRegistry().render();
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
            getScene().getSpriteBatch().destroy();
            getScene().destroy();
        }
    }

    public void startFrame() {
        if (transitioningScene) {
            currentSceneIndex = scenes.size() - 1;
            scenes.get(currentSceneIndex).initialize();

            if (currentSceneIndex > 0) {
                scenes.get(currentSceneIndex - 1).getSpriteBatch().destroy();
                scenes.get(currentSceneIndex - 1).destroy();
            }

            transitioningScene = false;
        }
    }

    /** Updates the registry of the scene. */
    public void endFrame() {
        if (scenes.size() > currentSceneIndex) {
            getScene().getRegistry().updateEntities();
        }
    }

    /** Gets the scene. */
    public Scene getScene() {
        return scenes.get(currentSceneIndex);
    }
}
