package com.ustudents.engine.ecs;

/** Defines a component from an ECS point of view (a structure of data). */
public class Component {
    /** The ID. */
    private int id;

    private Entity entity;

    /** Set the ID. */
    void setId(int id) {
        this.id = id;
    }

    /** @return the id of the component. */
    public int getId() {
        return id;
    }

    /**
     * Sets its entity (parent).
     *
     * @param entity The entity.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /** @return its entity (parent). */
    public Entity getEntity() {
        return entity;
    }

    /** @return its registry. */
    public Registry getRegistry() {
        return entity.getRegistry();
    }

    /** Called when a scene is loaded. */
    public void onSceneLoaded() {

    }

    /** Called after creation. */
    public void initialize() {

    }

    /** Called before destroying. */
    public void destroy() {

    }

    /** Called every frame to render ImGui content. */
    public void renderImGui() {

    }
}
