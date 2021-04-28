package com.ustudents.engine.ecs;

import com.ustudents.engine.scene.SceneManager;

import java.util.*;

/** Defines a system from an ECS point of view (a processing unit for every entities that have a matching signature). */
public class System {
    /** Defines a signature to keep track of which components are needed to be an entity in this system. */
    protected List<BitSet> signatures;

    /** The list of entity within this system. */
    protected Set<Entity> entities;

    /** The registry. */
    protected Registry registry;

    /** Class constructor. */
    public System() {
        signatures = new ArrayList<>();
        entities = new HashSet<>();
        this.registry = SceneManager.get().getRegistry();
    }

    /**
     * Updates the system's logic.
     *
     * @param dt The delta time.
     */
    public void update(float dt) {

    }

    /** Renders the system on the screen. */
    public void render() {

    }

    /**
     * Adds an entity to the system.
     *
     * @param entity The entity to add.
     */
    void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Removes an entity from the system.
     *
     * @param entity The entity to remove.
     */
    void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    /** @return all entities of this system. */
    public Set<Entity> getEntities() {
        return entities;
    }

    /**
     * Defines that we need a specific component type to be included within this system.
     * It will change the signature to keep track of which components are needed.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     */
    protected  <T extends Component> void requireComponent(Class<T> classType) {
        requireComponent(classType, 0);
    }

    /**
     * Defines that we need a specific component type to be included within this system.
     * It will change the signature to keep track of which components are needed.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     */
    protected  <T extends Component> void requireComponent(Class<T> classType, int signatureId) {
        while (signatures.size() <= signatureId) {
            signatures.add(new BitSet());
        }

        signatures.get(signatureId).set(registry.getComponentTypeRegistry().getIdForType(classType));
    }
}
