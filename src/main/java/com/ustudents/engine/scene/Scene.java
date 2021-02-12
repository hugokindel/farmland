package com.ustudents.engine.scene;

import com.ustudents.engine.ecs.system.SpriteRenderSystem;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.graphic.Camera;

/** Defines a scene element. */
public abstract class Scene {
    /** The registry for every entities in the scene. */
    protected Registry registry;

    /** The scene manager handling the scene. */
    protected SceneManager sceneManager;

    protected Camera camera;

    protected Spritebatch spriteBatch;

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
        this.spriteBatch = new Spritebatch(this.camera);
    }

    /** Initialize the scene (called when created). */
    public abstract void initialize();

    void _initialize() {
        registry.addSystem(SpriteRenderSystem.class);
        initialize();
    }

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

    public Spritebatch getSpriteBatch() {
        return spriteBatch;
    }
}
