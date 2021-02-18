package com.ustudents.farmland.entities;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.Registry;

/** Defines an entity property of a player. */
public abstract class Property extends Entity {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Property(Integer id, Registry registry) {
        super(id, registry);
    }

    /**
     * initialize the entity with component
     * with a priority of 10
     */
    public abstract void init();
}
