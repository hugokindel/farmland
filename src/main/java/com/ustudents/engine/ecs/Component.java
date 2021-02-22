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

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public Registry getRegistry() {
        return entity.getRegistry();
    }

    public void onSceneLoaded() {

    }

    public void initialize() {

    }

    public void destroy() {

    }

    public void renderImGui() {

    }
}
