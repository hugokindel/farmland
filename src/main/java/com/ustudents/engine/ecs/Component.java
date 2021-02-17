package com.ustudents.engine.ecs;

/** Defines a component from an ECS point of view (a structure of data). */
public class Component {
    /** The ID. */
    protected int id;

    protected Entity entity;

    /** Set the ID. */
    void setId(int id) {
        this.id = id;
    }

    /** @return the id of the component. */
    public int getId() {
        return id;
    }

    void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    /**
     * Converts to string.
     *
     * @return a string.
     */
    @Override
    public String toString() {
        return "Component{" +
                "id=" + id +
                '}';
    }

    public void renderImGui() {

    }
}
