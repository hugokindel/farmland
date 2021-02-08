package com.ustudents.farmland.core;

import com.ustudents.farmland.ecs.Registry;
import com.ustudents.farmland.graphics.Camera;

/** Defines a scene element. */
public abstract class Scene {
    /** The registry for every entities in the scene. */
    protected Registry registry;

    /** The scene manager handling the scene. */
    protected  SceneManager sceneManager;

    protected Camera camera;

    /**
     * Initialize the variables of the scene.
     *
     * @param sceneManager The scene manager.
     */
    void create(SceneManager sceneManager) {
        this.registry = new Registry();
        this.sceneManager = sceneManager;
        this.camera = new Camera(1000, 50, 2000);
        this.camera.setSize(1280, 720);
    }

    /** Initialize the scene (called when created). */
    public abstract void initialize();

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

    public Camera getCamera() {
        return camera;
    }
}
