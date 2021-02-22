package com.ustudents.engine.ecs;

import com.ustudents.engine.core.cli.print.Out;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Implements a pool of component data sorted by entity ID. */
public class ComponentPool<T extends Component> implements Pool {
    /** The components data. */
    private final ArrayList<T> data;

    /** The size of the data. */
    private int size;

    /** Map to keep track of entity IDs per indexes. */
    private final Map<Integer, Integer> entityIdToIndex;

    /** Map to keep track of indexes per entity IDs. */
    private final Map<Integer, Integer> indexToEntityId;

    /**
     * Class constructor.
     *
     * @param capacity The default capacity of the data array list.
     */
    public ComponentPool(int capacity) {
        size = 0;
        data = new ArrayList<>(capacity);
        entityIdToIndex = new HashMap<>();
        indexToEntityId = new HashMap<>();
    }

    /**
     * Removes an entity from the pool.
     *
     * @param entityId The entity ID.
     */
    public void removeEntityFromPool(int entityId) {
        if (entityIdToIndex.containsKey(entityId)) {
            remove(entityId);
        }
    }

    /** @return if the data is empty. */
    public boolean isEmpty() {
        return size == 0;
    }

    /** @return the size of the data. */
    public int size() {
        return size;
    }

    /** Clear all the data. */
    public void clear() {
        data.clear();
        entityIdToIndex.clear();
        indexToEntityId.clear();
        size = 0;
    }

    /**
     * Sets a component data entry for a specific entity ID.
     *
     * @param entityId The entity ID.
     * @param componentData The component data.
     */
    public void set(int entityId, T componentData) {
        if (entityIdToIndex.containsKey(entityId)) {
            Out.printlnWarning("You are trying to add a component from an entity, but this component already exists for this entity (you are overwriting it).");
            data.set(entityIdToIndex.get(entityId), componentData);
            return;
        }

        int index = size;
        entityIdToIndex.put(entityId, index);
        indexToEntityId.put(index, entityId);

        if (index >= data.size()) {
            data.ensureCapacity(size * 2);
            data.add(null);
        }

        data.set(index, componentData);
        size++;
    }

    /**
     * Removes a component data entry for a specific entity ID.
     *
     * @param entityId The entity ID.
     */
    public void remove(int entityId) {
        if (!containsEntity(entityId)) {
            Out.printlnWarning("You are trying to remove a component from an entity, but the component couldn't be found");
            return;
        }

        int indexOfRemoved = entityIdToIndex.get(entityId);
        int indexOfLast = size - 1;
        data.set(indexOfRemoved, data.get(indexOfLast));

        int entityIdOfLastElement = indexToEntityId.get(indexOfLast);
        entityIdToIndex.put(entityIdOfLastElement, indexOfRemoved);
        indexToEntityId.put(indexOfRemoved, entityIdOfLastElement);

        entityIdToIndex.remove(entityId);
        indexToEntityId.remove(indexOfLast);
        data.remove(data.size() - 1);
        size--;
    }

    /**
     * Check wether we contains a component for the specific entity ID or not.
     *
     * @param entityIndex The entity ID.
     *
     * @return if we contains it.
     */
    public boolean containsEntity(int entityIndex) {
        return entityIdToIndex.containsKey(entityIndex);
    }

    /**
     * Gets the component data at the given index.
     *
     * @param index The index.
     *
     * @return the data.
     */
    public T get(int index) {
        return data.get(index);
    }

    /**
     * Gets the component data at the given entity ID.
     *
     * @param entityId The entity ID.
     *
     * @return the data.
     */
    public T getFromEntity(int entityId) {
        return data.get(entityIdToIndex.get(entityId));
    }

    /**
     * Gets the component data at the given entity ID.
     *
     * @param entityId The entity ID.
     *
     * @return the data.
     */
    public T getFromEntitySafe(int entityId) {
        if (!entityIdToIndex.containsKey(entityId)) {
            return null;
        }

        return data.get(entityIdToIndex.get(entityId));
    }
}
