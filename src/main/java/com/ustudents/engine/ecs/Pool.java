package com.ustudents.engine.ecs;

/** Interface for pools containing entities. */
public interface Pool {
    /**
     * Removes an entity from the pool.
     *
     * @param entityId The entity ID.
     */
    void removeEntityFromPool(int entityId);
}
