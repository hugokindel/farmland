package com.ustudents.farmland.entities;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;

/** Defines an entity element for a map. */
public abstract class MapElement extends Entity {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public MapElement(Integer id, Registry registry) {
        super(id, registry);
    }

    /** initialize the entity with component */
    public abstract void init();
}
