package com.ustudents.engine.ecs;

import com.ustudents.engine.Game;
import com.ustudents.engine.scene.Scene;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/** Defines a system from an ECS point of view (a processing unit for every entities that have a matching signature). */
public class System {
    /** Defines a signature to keep track of which components are needed to be an entity in this system. */
    protected BitSet signature;

    /** The list of entity within this system. */
    protected List<Entity> entities;

    /** The registry. */
    protected Registry registry;

    /** Class constructor. */
    public System(Registry registry) {
        signature = new BitSet();
        entities = new ArrayList<>();
        this.registry = registry;
    }

    public void update(double dt) {

    }

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
    public List<Entity> getEntities() {
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
        signature.set(registry.getComponentTypeRegistry().getIdForType(classType));
    }

    protected Scene getScene() {
        return Game.get().getSceneManager().getScene();
    }
}
