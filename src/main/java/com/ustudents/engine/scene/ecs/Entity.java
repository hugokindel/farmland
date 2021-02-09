package com.ustudents.engine.scene.ecs;

import java.util.List;
import java.util.Set;

/** Defines an entity object from an ECS point of view. */
public class Entity {
    /** The unique ID. */
    private final Integer id;

    /** The registry used to interact with this entity. */
    private final Registry registry;

    /**
     * Class constructor.
     *
     * @param id The ID.
     * @param registry The registry.
     */
    public Entity(Integer id, Registry registry) {
        this.id = id;
        this.registry = registry;
    }

    /** Kills it. */
    public void kill() {
        registry.killEntity(this);
    }

    /**
     * Sets its name.
     *
     * @param name The name.
     */
    public void setName(String name) {
        registry.setNameOfEntity(this, name);
    }

    /** @return its name. */
    public String getName() {
        return registry.getNameOfEntity(this);
    }

    public String getNameOrIdentifier() {
        return hasName() ? getName() : getClass().getSimpleName() + getId();
    }

    /** @return if it has a name. */
    public boolean hasName() {
        return registry.entityHasName(this);
    }

    /** Removes its name (if it has one). */
    public void removeName() {
        registry.removeNameFromEntity(this);
    }

    /**
     * Adds a tag (useful for grouping entities).
     *
     * @param tag The tag.
     */
    public void addTag(String tag) {
        registry.addEntityToTag(this, tag);
    }

    /** @return its tags. */
    public Set<String> getTags() {
        return registry.getTagsOfEntity(this);
    }

    /**
     * Check whether it has a specific tag or not.
     *
     * @param tag The tag.
     *
     * @return if it has a tag or not.
     */
    public boolean hasTag(String tag) {
        return registry.entityHasTag(this, tag);
    }

    /**
     * Removes a specific tag.
     *
     * @param tag The tag.
     */
    public void removeTag(String tag) {
        registry.removeTagFromEntity(this, tag);
    }

    /** Removes all tags (if it has any). */
    public void removeAllTags() {
        registry.removeAllTagsFromEntity(this);
    }

    /**
     * Set its parent (useful to arrange entities like trees).
     *
     * @param parent The parent entity.
     */
    public void setParent(Entity parent) {
        registry.setParentOfEntity(this, parent);
    }

    /** @return its parent. */
    public Entity getParent() {
        return registry.getParentOfEntity(this);
    }

    /** @return if it has a parent. */
    public boolean hasParent() {
        return registry.entityHasParent(this);
    }

    /** Removes its parent. */
    public void removeParent() {
        registry.removeParentFromEntity(this);
    }

    /** @return a set of its children. */
    public List<Entity> getChildren() {
        return registry.getChildrenOfEntity(this);
    }

    /**
     * Adds a component of a given type.
     *
     * @param classType The component type class.
     * @param args The component constructor arguments (do not use primitive types).
     * @param <T> The component type.
     */
    public <T extends Component> void addComponent(Class<T> classType, Object... args) {
        registry.addComponentToEntity(this, classType, args);
    }

    /**
     * Gets the component of a given type.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     *
     * @return the component.
     */
    public <T extends Component> T getComponent(Class<T> classType) {
        return registry.getComponentOfEntity(this, classType);
    }

    /** @return a set of its components. */
    public Set<Component> getComponents() {
        return registry.getComponentsOfEntity(this);
    }

    /** @return the number of components it possess. */
    public int getNumberOfComponents() {
        return registry.getNumberOfComponentsOfEntity(this);
    }

    /**
     * Check whether it possess a specific component type or not.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     *
     * @return if it has the component.
     */
    public <T extends Component> boolean hasComponent(Class<T> classType) {
        return registry.entityHasComponent(this, classType);
    }

    /**
     * Removes a specific component of the given type.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     */
    public <T extends Component> void removeComponent(Class<T> classType) {
        registry.removeComponentFromEntity(this, classType);
    }

    /** @return its ID. */
    public int getId() {
        return id;
    }

    /** @return its registry. */
    public Registry getRegistry() {
        return registry;
    }

    /**
     * Converts to string.
     *
     * @return a string.
     */
    @Override
    public String toString() {
        return "Entity{" +
                "id=" + id +
                ", registry=" + registry +
                (hasName() ? ", name=" + getName() : "") +
                ", tags=" + getTags() +
                (hasParent() ? ", parentId=" + getParent().getId() : "") +
                (getChildren().size() > 0 ? ", children=" + getChildren() : "") +
                ", components=" + getComponents() +
                '}';
    }
}