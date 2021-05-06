package com.ustudents.engine.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.window.Window;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.System;
import com.ustudents.engine.ecs.system.BehaviourSystem;
import com.ustudents.engine.ecs.system.WorldRenderSystem;
import com.ustudents.engine.ecs.system.UiRenderSystem;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.graphic.Camera;
import org.joml.Vector2i;

import java.util.Map;

/** Defines a scene element. */
public abstract class Scene {
    /** The registry for every entities in the scene. */
    protected Registry registry;

    /** The scene manager handling the scene. */
    protected SceneManager sceneManager;

    /** The world camera (in world coordinates). */
    protected Camera camera;

    /** The UI camera (in screen coordinates). */
    protected Camera uiCamera;

    /** The cursor camera (only used for cursor rendering, in screen coordinates). */
    protected Camera cursorCamera;

    /** The spritebatch (the main renderer). */
    protected Spritebatch spritebatch;

    protected boolean forceImGui;

    /**
     * Initialize the variables of the scene.
     *
     * @param sceneManager The scene manager.
     */
     void create(SceneManager sceneManager) {
         Vector2i size = Game.get().getWindow().getSize();

         this.registry = SceneManager.get().getRegistry();
         this.sceneManager = sceneManager;
         this.camera = new Camera(100, 0.005f, 0.01f, Camera.Type.World);
         this.camera.resize(size.x, size.y);
         this.uiCamera = new Camera(100, 0.005f, 0.01f, Camera.Type.UI);
         this.uiCamera.resize(size.x, size.y);
         this.cursorCamera = new Camera(100, 0.005f, 0.01f, Camera.Type.Cursor);
         this.cursorCamera.resize(size.x, size.y);
         if (Game.get().canRender()) {
             this.spritebatch = new Spritebatch(this.camera);
         }
         this.forceImGui = false;
    }

    /** Initialize the scene (called when created). */
    public void initialize() {

    }

    /** Updates the logic every frame. */
    public void update(float dt) {

    }

    /** Renders the scene every frame. */
    public void render() {

    }

    /** Renders the game (ImGui specifics). */
    public void renderImGui() {

    }

    /** Destroys the scene (called when changing scene or quitting the game). */
    public void destroy() {

    }

    /**
     * Changes the current scene to the given scene.
     *
     * @param scene The new current scene.
     */
    public <T extends Scene> void changeScene(T scene) {
        sceneManager.changeScene(scene);
    }

    public <T extends Scene> void changeScene(T scene, boolean addToTypeStack) {
        sceneManager.changeScene(scene, addToTypeStack);
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
            sceneManager.changeScene(classType);
        } else {
            sceneManager.changeScene(classType, args);
        }
    }

    public <T extends System> T addSystem(T system) {
        return registry.addSystem(system);
    }

    @Deprecated
    public <T extends System> T addSystem(Class<T> classType, Object... args) {
        if (args.length == 0) {
            return registry.addSystem(classType);
        } else {
            return registry.addSystem(classType, args);
        }
    }

    public <T extends System> void removeSystem(Class<T> classType) {
        registry.removeSystem(classType);
    }

    public <T extends System> boolean hasSystem(Class<T> classType) {
        return registry.hasSystem(classType);
    }

    public <T extends System> T getSystem(Class<T> classType) {
        return registry.getSystem(classType);
    }

    @Deprecated
    public <T extends Entity> T createEntity(Class<T> classType, Object... args) {
        if (args.length == 0) {
            return registry.addEntity(classType);
        } else {
            return registry.addEntity(classType, args);
        }
    }

    public Entity createEntity() {
        return registry.addEntity();
    }

    @Deprecated
    public <T extends Entity> T createEntityWithName(String name, Class<T> classType, Object... args) {
        if (args.length == 0) {
            return registry.addEntityWithName(name, classType);
        } else {
            return registry.addEntityWithName(name, classType, args);
        }
    }

    public Entity createEntityWithName(String name) {
        return registry.addEntityWithName(name);
    }

    public void killEntity(Entity entity) {
        registry.killEntity(entity);
    }

    public void killEntity(Entity entity, boolean updateRegistry) {
        registry.killEntity(entity, updateRegistry);
    }

    public boolean hasEntityWithName(String name) {
        return registry.entityWithNameExists(name);
    }

    public Map<Integer, Entity> getEntities() {
        return registry.getEntities();
    }

    public Entity getEntityByName(String name) {
        return registry.getEntityByName(name);
    }

    public void updateRegistry() {
        registry.updateEntities();
    }

    /** Quit the game. */
    public void quit() {
        Game.get().quit();
    }

    /** @return the registry of the scene. */
    public Registry getRegistry() {
        return registry;
    }

    /** @return the game. */
    public Game getGame() {
        return Game.get();
    }

    /** @return the game's window. */
    public Window getWindow() {
        return Game.get().getWindow();
    }

    /** @return the game's scene manager. */
    public SceneManager getSceneManager() {
        return sceneManager;
    }

    /** @return the world camera. */
    public Camera getWorldCamera() {
        return camera;
    }

    /** @return the UI camera. */
    public Camera getUiCamera() {
        return uiCamera;
    }

    /** @return the cursor camera. */
    public Camera getCursorCamera() {
        return uiCamera;
    }

    /** @return the spritebatch (the main renderer). */
    public Spritebatch getSpritebatch() {
        return spritebatch;
    }

    /** Initialize the scene internally. */
    void initializeInternals() {
        registry.addSystem(new BehaviourSystem());

        if (Game.get().canRender()) {
            registry.addSystem(new WorldRenderSystem());
            registry.addSystem(new UiRenderSystem());
        }

        initialize();
    }

    public boolean isForceImGuiEnabled() {
        return forceImGui;
    }
}
