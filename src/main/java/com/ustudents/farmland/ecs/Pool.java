package com.ustudents.farmland.ecs;

/** Interface for pools containing entities. */
public interface Pool {
    /**
     * Removes an entity from the pool.
     *
     * @param entityId The entity ID.
     */
    void removeEntityFromPool(int entityId);
}
