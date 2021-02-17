package com.ustudents.engine.scene;

import com.ustudents.engine.ecs.system.BehaviourSystem;
import com.ustudents.engine.ecs.system.GameRenderSystem;
import com.ustudents.engine.ecs.system.RenderSystem;
import com.ustudents.engine.ecs.system.UiRenderSystem;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.graphic.Camera;
import com.ustudents.farmland.Farmland;
import org.joml.Vector2i;

/** Defines a scene element. */
public abstract class Scene {
    /** The registry for every entities in the scene. */
    protected Registry registry;

    /** The scene manager handling the scene. */
    protected SceneManager sceneManager;

    protected Camera camera;

    protected Camera uiCamera;

    protected Camera cursorCamera;

    protected Spritebatch spritebatch;

    /**
     * Initialize the variables of the scene.
     *
     * @param sceneManager The scene manager.
     */
     void create(SceneManager sceneManager) {
         Vector2i size = Farmland.get().getWindow().getSize();

         this.registry = new Registry();
         this.sceneManager = sceneManager;
         this.camera = new Camera(100, 0.005f, 0.01f, Camera.Type.World);
         this.camera.setSize(size.x, size.y);
         this.uiCamera = new Camera(100, 0.005f, 0.01f, Camera.Type.UI);
         this.uiCamera.setSize(size.x, size.y);
         this.cursorCamera = new Camera(100, 0.005f, 0.01f, Camera.Type.Cursor);
         this.cursorCamera.setSize(size.x, size.y);
         this.spritebatch = new Spritebatch(this.camera);
    }

    /** Initialize the scene (called when created). */
    public abstract void initialize();

    void _initialize() {
        registry.addSystem(BehaviourSystem.class);
        registry.addSystem(GameRenderSystem.class);
        registry.addSystem(UiRenderSystem.class);

        initialize();
    }

    /** Updates the logic every frame. */
    public abstract void update(float dt);

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

    public Camera getUiCamera() {
        return uiCamera;
    }

    public Camera getCursorCamera() {
        return uiCamera;
    }

    public Spritebatch getSpritebatch() {
        return spritebatch;
    }
}
