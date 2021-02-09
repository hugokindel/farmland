package com.ustudents.engine.ecs;

import java.util.HashMap;

/** Static class to keep track of every component type used (to allocate one ID per component type). */
public class ComponentTypeRegistry {
    /** The next ID to use (for the next component type). */
    private int nextId;

    /** A map containing the list of IDs per type name already processed. */
    private final HashMap<String, Integer> types;

    /** Class constructor. */
    public ComponentTypeRegistry() {
        nextId = 0;
        types = new HashMap<>();
    }

    /**
     * Gets the ID of a specific component type.
     *
     * @param classType The component type class.
     * @param <T> The component type.
     *
     * @return the ID.
     */
    public <T extends Component> int getIdForType(Class<T> classType) {
        String className = classType.getName();

        if (types.containsKey(className)) {
            return types.get(className);
        }

        int id = nextId++;
        types.put(className, id);
        return id;
    }
}
