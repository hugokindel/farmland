package com.ustudents.farmland.core;

import com.ustudents.farmland.ecs.Registry;

/** Defines a scene element. */
public abstract class Scene {
    /** The registry for every entities in the scene. */
    protected Registry registry;

    /** The scene manager handling the scene. */
    protected  SceneManager sceneManager;

    /**
     * Initialize the variables of the scene.
     *
     * @param sceneManager The scene manager.
     */
    void create(SceneManager sceneManager) {
        this.registry = new Registry();
        this.sceneManager = sceneManager;
    }

    /** Initialize the scene (called when created). */
    public abstract void initialize();

    /** Processes the input every frame. */
    public abstract void processInput();

    /** Updates the logic every frame. */
    public abstract void update(double dt);

    /** Renders the scene every frame. */
    public abstract void render();

    public void renderImGui() {

    }

    /** Destroys the scene (called when changing scene or quitting the game). */
    public abstract void destroy();

    /** @return the registry of the scene. */
    public Registry getRegistry() {
        return registry;
    }

    /** @return the scene manager of the scene. */
    public SceneManager getSceneManager() {
        return sceneManager;
    }
}
