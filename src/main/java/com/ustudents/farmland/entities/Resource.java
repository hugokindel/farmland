package com.ustudents.farmland.entities;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;

/** Defines an entity resource of a player. */
public abstract class Resource extends Entity {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Resource(Integer id, Registry registry) {
        super(id, registry);
    }

    /** initialize the entity with component */
    public abstract void init();
}
