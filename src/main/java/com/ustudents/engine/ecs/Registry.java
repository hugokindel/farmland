package com.ustudents.engine.ecs;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.core.cli.print.style.Style;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.graphic.RenderableComponent;
import com.ustudents.engine.utility.TypeUtil;

import java.util.*;

/** Defines the main handler of every elements of the ECS. */
@SuppressWarnings({"unused", "unchecked"})
public class Registry {
    /** The total number of entities living. */
    private int totalNumberOfEntities;

    /** The last used entity number (ID - 1), used to keep track of entity ID to use. */
    private int lastEntityNumber;

    /** Map to keep track of systems per ID. */
    private final Map<Integer, System> systems;

    /** Map to keep track of entity per ID. */
    private final Map<Integer, Entity> entityPerIndex;

    /** Map to keep track of entity signature per ID. */
    private final Map<Integer, BitSet> signaturePerEntity;

    /** Map to keep track of entities per name. */
    private final Map<String, Set<Entity>> entitiesPerName;

    /** Map to keep track of name per entity. */
    private final Map<Integer, String> namePerEntity;

    /** Map to keep track of entities per tag. */
    private final Map<String, Set<Entity>> entitiesPerTag;

    /** Map to keep track of tags per entity. */
    private final Map<Integer, Set<String>> tagsPerEntity;

    /** Map to keep track parent per entity. */
    private final Map<Integer, Entity> parentPerEntity;

    /** Map to keep track of children per entity. */
    private final Map<Integer, List<Entity>> childrenPerEntity;

    /** Map to keep track of components per entity. */
    private final Map<Integer, Set<Component>> componentsPerEntity;

    /** Map to keep track of renderable components per entity. */
    private final Map<Entity, Set<RenderableComponent>> renderableComponentsPerEntity;

    /** Map to keep track of behaviour components per entity. */
    private final Map<Entity, Set<BehaviourComponent>> behaviourComponentsPerEntity;

    /** Set to keep track of entities at root (with no parent). */
    private final List<Entity> entitiesAtRoot;

    /**
     * Set to keep track of entities that needs to be added to the systems (after being created),
     * to make sure it does not perturb any game loop.
     */
    private final Set<Entity> entitiesToBeAddedToSystems;

    /**
     * Set to keep track of entities that needs to be deleted from the systems and memory (after being killed),
     * to make sure it does not perturb any game loop.
     */
    private final Set<Entity> entitiesToBeRemovedFromEverything;

    private final Set<Entity> entitiesToCheckStateForSystems;

    /** Set to keep track of entities that needs to be removed from the systems (generally only disabled entities). */
    private final Set<Entity> entitiesToBeRemovedFromSystems;

    /** Set to keep track of enabled entities. */
    private final Set<Entity> enabledEntities;

    /** Set to keep track of deleted entities. */
    private final Set<Entity> disabledEntities;

    /** Map to keep track of entity per ID. */
    private final Set<Entity> entitiesToKeepBetweenLoads;

    private final Set<System> systemsToKeepBetweenLoads;

    /** List to keep track of all component pools (list of components per type). */
    private final List<Pool> componentPools;

    /** Deque to keep track of IDs that can be reused (meaning they were recently freed from killed entities). */
    private final Deque<Integer> freeIds;

    /** The component type registry to keep track of component IDs. */
    private final ComponentTypeRegistry componentTypeRegistry;

    /** Value to use as a base capacity for component pools. */
    private final int baseComponentPoolCapacity;

    /** Class constructor. */
    public Registry() {
        totalNumberOfEntities = 1; // There always is a camera.
        systems = new LinkedHashMap<>();
        entityPerIndex = new HashMap<>();
        signaturePerEntity = new HashMap<>();
        entitiesPerName = new HashMap<>();
        namePerEntity = new HashMap<>();
        entitiesPerTag = new HashMap<>();
        tagsPerEntity = new HashMap<>();
        parentPerEntity = new HashMap<>();
        childrenPerEntity = new HashMap<>();
        componentsPerEntity = new HashMap<>();
        renderableComponentsPerEntity = new HashMap<>();
        behaviourComponentsPerEntity = new HashMap<>();
        entitiesAtRoot = new ArrayList<>();
        entitiesToBeAddedToSystems = new HashSet<>();
        entitiesToBeRemovedFromEverything = new HashSet<>();
        entitiesToCheckStateForSystems = new HashSet<>();
        entitiesToBeRemovedFromSystems = new HashSet<>();
        enabledEntities = new HashSet<>();
        disabledEntities = new HashSet<>();
        entitiesToKeepBetweenLoads = new HashSet<>();
        systemsToKeepBetweenLoads = new HashSet<>();
        componentPools = new ArrayList<>();
        freeIds = new ArrayDeque<>();
        componentTypeRegistry = new ComponentTypeRegistry();
        baseComponentPoolCapacity = 64;
    }

    /**
     * Adds a system.
     *
     * @param system The system.
     * @param <T> The type of the system.
     *
     * @return the system.
     */
    public <T extends System> T addSystem(T system) {
        int systemId = system.getClass().getName().hashCode();

        systems.put(systemId, system);

        return system;
    }

    /**
     * Adds a system of given type.
     *
     * @param classType The system type class.
     * @param args The system type constructor arguments.
     * @param <T> The system type.
     *
     * @return the system.
     */
    @Deprecated
    public <T extends System> T addSystem(Class<T> classType, Object... args) {
        if (args.length == 0) {
            return addSystem(Objects.requireNonNull(TypeUtil.createInstance(classType)));
        }

        return addSystem(Objects.requireNonNull(TypeUtil.createInstance(classType, this, args)));
    }

    /**
     * Removes a system.
     *
     * @param classType The system type class.
     * @param <T> The system type.
     */
    public <T extends System> void removeSystem(Class<T> classType) {
        int systemId = classType.getName().hashCode();

        systems.remove(systemId);
    }

    /**
     * Check whether is possess a specific system.
     *
     * @param classType The system type class.
     * @param <T> The system type.
     *
     * @return if it has the system.
     */
    public <T extends System> boolean hasSystem(Class<T> classType) {
        int systemId = classType.getName().hashCode();

        return systems.containsKey(systemId);
    }

    /**
     * Gets the system.
     *
     * @param classType The system type class.
     * @param <T> The system type.
     *
     * @return the system.
     */
    public <T extends System> T getSystem(Class<T> classType) {
        int systemId = classType.getName().hashCode();

        return (T)systems.get(systemId);
    }

    /**
     * Adds an entity.
     *
     * @return the entity.
     */
    public Entity addEntity() {
        return addEntity(new Entity());
    }

    /**
     * Adds an entity.
     *
     * @param entity the entity.
     * @param <T> The entity type.
     *
     * @return the entity.
     */
    public <T extends Entity> T addEntity(T entity) {
        int entityId = entity.getId();

        entityPerIndex.put(entityId, entity);
        entitiesAtRoot.add(entity);
        enabledEntities.add(entity);
        signaturePerEntity.put(entityId, new BitSet());
        entitiesToBeAddedToSystems.add(entity);
        componentsPerEntity.put(entityId, new HashSet<>());
        renderableComponentsPerEntity.put(entity, new HashSet<>());
        behaviourComponentsPerEntity.put(entity, new HashSet<>());
        totalNumberOfEntities++;

        if (Game.isDebugging()) {
            Out.printlnDebug("entity " + Style.Bold + entityId + Style.Reset + ": created");
        }

        return entity;
    }

    /**
     * Adds an entity of a specific subtype.
     *
     * @param classType The entity type class.
     * @param args The entity type constructor arguments.
     * @param <T> The entity type.
     *
     * @return the entity.
     */
    @Deprecated
    public <T extends Entity> T addEntity(Class<T> classType, Object... args) {
        if (args.length == 0) {
            return addEntity(Objects.requireNonNull(TypeUtil.createInstance(classType)));
        }

        return addEntity(Objects.requireNonNull(TypeUtil.createInstance(classType, args)));
    }

    /**
     * Creates an entity.
     *
     * @return the entity.
     */
    public Entity addEntityWithName(String name) {
        return addEntityWithName(name, new Entity());
    }

    /**
     * Adds an entity with the given name.
     *
     * @param name The name.
     * @param entity The entity.
     * @param <T> The entity type.
     *
     * @return the entity.
     */
    public <T extends Entity> T addEntityWithName(String name, T entity) {
        addEntity(entity);
        entity.setName(name);
        return entity;
    }

    /**
     * Adds an entity of the given type with the given name.
     *
     * @param name The name.
     * @param classType The entity type class.
     * @param args The entity type constructor arguments.
     * @param <T> The entity type.
     *
     * @return the entity.
     */
    @Deprecated
    public <T extends Entity> T addEntityWithName(String name, Class<T> classType, Object... args) {
        T entity;

        if (args.length == 0) {
            entity = addEntity(classType);
        } else {
            entity = addEntity(classType, args);
        }

        entity.setName(name);

        return entity;
    }

    /**
     * Kills an entity (it will be deleted at the next registry update).
     *
     * @param entity The entity.
     */
    public void killEntity(Entity entity) {
        killEntity(entity, false);
    }

    public void killEntity(Entity entity, boolean updateRegistry) {
        entitiesToBeRemovedFromEverything.add(entity);
        totalNumberOfEntities--;

        if (Game.isDebugging()) {
            Out.printlnDebug("entity " + Style.Bold + entity.getId() + Style.Reset + ": killed");
        }

        if (updateRegistry) {
            updateEntities();
        }
    }

    /**
     * Checks if an entity with the given name exists.
     *
     * @param name The name.
     *
     * @return if it exists.
     */
    public boolean entityWithNameExists(String name) {
        return entitiesPerName.containsKey(name);
    }

    /** @return all the entities. */
    public Map<Integer, Entity> getEntities() {
        return entityPerIndex;
    }

    /**
     * Returns an entity by ID.
     *
     * @param index The ID.
     *
     * @return the entity.
     */
    public Entity getEntityById(int index) {
        return entityPerIndex.get(index);
    }

    /**
     * Gets all entity by tag.
     *
     * @param tag The tag.
     *
     * @return the entities.
     */
    public Set<Entity> getEntitiesByTag(String tag) {
        return entitiesPerTag.get(tag);
    }

    /**
     * Returns an entity by name.
     *
     * @param name The name.
     *
     * @return the entity.
     */
    public Entity getEntityByName(String name) {
        for (Entity entity : entitiesPerName.get(name)) {
            return entity;
        }

        return null;
    }

    /**
     * Gets all entity by name.
     *
     * @param name The name.
     *
     * @return the entities.
     */
    public Set<Entity> getEntitiesByName(String name) {
        return entitiesPerName.get(name);
    }

    /**
     * Gets all entity at root (with no parents).
     *
     * @return a set of entity.
     */
    public List<Entity> getEntitiesAtRoot() {
        return entitiesAtRoot;
    }

    /**
     * Enables or disables an entity (it will not be acting within the systems if it is disabled).
     *
     * @param entity The entity.
     * @param enable If we should enable it or not.
     */
    public void setEnabledEntity(Entity entity, boolean enable) {
        if (enable == entity.isEnabled()) {
            return;
        }

        if (enable) {
            enabledEntities.add(entity);
            disabledEntities.remove(entity);
            entitiesToBeAddedToSystems.add(entity);
        } else {
            enabledEntities.remove(entity);
            disabledEntities.add(entity);
            entitiesToBeRemovedFromSystems.add(entity);
        }

        for (Entity child : entity.getChildren()) {
            child.setEnabled(enable);
        }
    }

    /** @return if the entity is enabled. */
    public boolean isEntityEnabled(Entity entity) {
        return enabledEntities.contains(entity);
    }

    /**
     * Sets the name of an entity.
     *
     * @param entity The entity.
     * @param name The name.
     */
    public void setNameOfEntity(Entity entity, String name) {
        if (!entitiesPerName.containsKey(name)) {
            entitiesPerName.put(name, new HashSet<>());
        }

        entitiesPerName.get(name).add(entity);
        namePerEntity.put(entity.getId(), name);
    }

    /**
     * Gets the name of an entity.
     *
     * @param entity The entity.
     *
     * @return the name.
     */
    public String getNameOfEntity(Entity entity) {
        int entityId = entity.getId();

        if (!namePerEntity.containsKey(entityId)) {
            return null;
        }

        return namePerEntity.get(entityId);
    }

    /**
     * Checks if the entity has a name.
     *
     * @param entity The entity.
     *
     * @return if it has a name.
     */
    public boolean entityHasName(Entity entity) {
        return namePerEntity.containsKey(entity.getId());
    }

    /**
     * Removes the name from the entity.
     *
     * @param entity The entity.
     */
    public void removeNameFromEntity(Entity entity) {
        int entityId = entity.getId();

        if (namePerEntity.containsKey(entityId)) {
            String name = namePerEntity.get(entityId);
            entitiesPerName.get(name).remove(entity);

            if (entitiesPerName.get(name).isEmpty()) {
                entitiesPerName.remove(name);
            }

            namePerEntity.remove(entityId);
        }
    }

    /**
     * Adds a tag to the entity.
     *
     * @param entity The entity.
     * @param tag The tag.
     */
    public void addEntityToTag(Entity entity, String tag) {
        int entityId = entity.getId();

        if (!entitiesPerTag.containsKey(tag)) {
            entitiesPerTag.put(tag, new HashSet<>());
        }

        entitiesPerTag.get(tag).add(entity);

        if (!tagsPerEntity.containsKey(entityId)) {
            tagsPerEntity.put(entityId, new HashSet<>());
        }

        tagsPerEntity.get(entityId).add(tag);
    }


    /**
     * Gets all the tags of the entity.
     *
     * @param entity The entity.
     *
     * @return the tags.
     */
    public Set<String> getTagsOfEntity(Entity entity) {
        int entityId = entity.getId();

        if (!tagsPerEntity.containsKey(entityId)) {
            return new HashSet<>();
        }

        return tagsPerEntity.get(entityId);
    }

    /**
     * Checks if the entity has a specific tag.
     *
     * @param entity The entity.
     * @param tag The tag.
     *
     * @return if it has the tag.
     */
    public boolean entityHasTag(Entity entity, String tag) {
        return entitiesPerTag.containsKey(tag) && entitiesPerTag.get(tag).contains(entity);
    }

    /**
     * Removes the tag from the entity (if it has it).
     *
     * @param entity The entity.
     * @param tag The tag.
     */
    public void removeTagFromEntity(Entity entity, String tag) {
        int entityId = entity.getId();

        if (tagsPerEntity.containsKey(entityId)) {
            tagsPerEntity.get(entityId).remove(tag);
        }

        if (entitiesPerTag.containsKey(tag)) {
            entitiesPerTag.get(tag).remove(entity);
        }
    }

    /**
     * Removes all tags from the entity (if it has any).
     *
     * @param entity The entity.
     */
    public void removeAllTagsFromEntity(Entity entity) {
        int entityId = entity.getId();

        if (tagsPerEntity.containsKey(entityId)) {
            Set<String> tags = tagsPerEntity.get(entityId);

            for (String tag : tags) {
                if (entitiesPerTag.containsKey(tag)) {
                    entitiesPerTag.get(tag).remove(entity);

                    if (entitiesPerTag.get(tag).isEmpty()) {
                        entitiesPerTag.remove(tag);
                    }
                }

                tagsPerEntity.get(entityId).remove(tag);
            }

            if (tagsPerEntity.get(entityId).isEmpty()) {
                tagsPerEntity.remove(entityId);
            }
        }
    }

    /**
     * Defines if we should keep the entity betweeen scenes.
     *
     * @param entity The entity.
     * @param keep If it should be kept.
     */
    public void keepEntityOnLoad(Entity entity, boolean keep) {
        if (keep) {
            entitiesToKeepBetweenLoads.add(entity);
        } else {
            entitiesToKeepBetweenLoads.remove(entity);
        }
    }

    /** @return if we should keep the entity between scenes. */
    public boolean isEntityKeptOnLoad(Entity entity) {
        return entitiesToKeepBetweenLoads.contains(entity);
    }

    /**
     * Sets the parent of the entity.
     *
     * @param entity The entity.
     * @param parentEntity The parent entity.
     */
    public void setParentOfEntity(Entity entity, Entity parentEntity) {
        int entityId = entity.getId();
        int parentId = parentEntity.getId();

        if (parentPerEntity.containsKey(entityId)) {
            int originalParentId = parentPerEntity.get(entityId).getId();

            childrenPerEntity.get(originalParentId).remove(entity);

            if (childrenPerEntity.get(originalParentId).isEmpty()) {
                childrenPerEntity.remove(originalParentId);
            }
        } else {
            entitiesAtRoot.remove(entity);
        }

        parentPerEntity.put(entityId, parentEntity);

        if (!childrenPerEntity.containsKey(parentId)) {
            childrenPerEntity.put(parentId, new ArrayList<>());
        }

        childrenPerEntity.get(parentId).add(entity);
    }

    /**
     * Gets the parent of the entity.
     *
     * @param entity The entity.
     *
     * @return the parent.
     */
    public Entity getParentOfEntity(Entity entity) {
        int entityId = entity.getId();

        return parentPerEntity.get(entityId);
    }

    /**
     * Checks if the entity has a parent.
     *
     * @param entity The entity.
     *
     * @return if it has a parent.
     */
    public boolean entityHasParent(Entity entity) {
        int entityId = entity.getId();

        return parentPerEntity.containsKey(entityId);
    }

    /**
     * Removes the parent from the entity (if it has one).
     * 
     * @param entity The entity.
     */
    public void removeParentFromEntity(Entity entity) {
        int entityId = entity.getId();

        if (parentPerEntity.containsKey(entityId)) {
            int originalParentId = parentPerEntity.get(entityId).getId();

            childrenPerEntity.get(originalParentId).remove(entity);

            if (childrenPerEntity.get(originalParentId).isEmpty()) {
                childrenPerEntity.remove(originalParentId);
            }
        }

        parentPerEntity.remove(entityId);
        entitiesAtRoot.add(entity);
    }

    /**
     * Gets the children from the entity.
     *
     * @param entity The entity.
     * @return a set of children.
     */
    public List<Entity> getChildrenOfEntity(Entity entity) {
        int entityId = entity.getId();

        if (!childrenPerEntity.containsKey(entityId)) {
            return new ArrayList<>();
        }

        return childrenPerEntity.get(entityId);
    }

    /**
     * Add a component to the entity.
     *
     * @param entity The entity.
     * @param component The component.
     */
    public <T extends Component> T addComponentToEntity(Entity entity, T component) {
        Class<?> classType = component.getClass();

        int entityId = entity.getId();
        int componentId = componentTypeRegistry.getIdForType(component.getClass());

        while (componentPools.size() <= componentId) {
            componentPools.add(new ComponentPool<T>(baseComponentPoolCapacity));
        }

        component.setId(componentTypeRegistry.getIdForType(component.getClass()));
        component.setEntity(entity);
        ((ComponentPool<T>)componentPools.get(componentId)).set(entityId, component);
        signaturePerEntity.get(entityId).set(componentId);
        componentsPerEntity.get(entityId).add(component);

        component.initialize();

        if (component instanceof BehaviourComponent) {
            behaviourComponentsPerEntity.get(entity).add((BehaviourComponent) component);
        }

        if (component instanceof RenderableComponent) {
            renderableComponentsPerEntity.get(entity).add((RenderableComponent) component);
        }

        if (Game.isDebugging()) {
            Out.printlnDebug("entity " + Style.Bold + entity.getId() + Style.Reset + ": component " +
                    Style.Bold + component.getId() + Style.Reset + ": added");
        }

        entitiesToBeAddedToSystems.add(entity);

        return component;
    }

    /**
     * Add a component of a given type to the entity.
     * 
     * @param entity The entity.
     * @param classType The component type class.
     * @param args The component type constructor arguments.
     * @param <T> The component type.
     */
    @Deprecated
    public <T extends Component> T addComponentToEntity(Entity entity, Class<T> classType, Object... args) {
        if (args.length == 0) {
            return addComponentToEntity(entity, Objects.requireNonNull(TypeUtil.createInstance(classType)));
        }

        return addComponentToEntity(entity, Objects.requireNonNull(TypeUtil.createInstance(classType, args)));
    }

    /**
     * Gets the component of a given type of the entity.
     * 
     * @param entity The entity.
     * @param classType The component type class.
     * @param <T> The component type.
     *           
     * @return the component.
     */
    public <T extends Component> T getComponentOfEntity(Entity entity, Class<T> classType) {
        int entityId = entity.getId();
        int componentId = componentTypeRegistry.getIdForType(classType);

        return ((ComponentPool<T>)componentPools.get(componentId)).getFromEntity(entityId);
    }

    public <T extends Component> T getComponentOfEntitySafe(Entity entity, Class<T> classType) {
        int entityId = entity.getId();
        int componentId = componentTypeRegistry.getIdForType(classType);

        return ((ComponentPool<T>)componentPools.get(componentId)).getFromEntitySafe(entityId);
    }

    /**
     * Gets all components of the entity.
     * 
     * @param entity The entity.
     *               
     * @return the components.
     */
    public Set<Component> getComponentsOfEntity(Entity entity) {
        return componentsPerEntity.get(entity.getId());
    }

    public Set<RenderableComponent> getRenderableComponentsOfEntity(Entity entity) {
        return renderableComponentsPerEntity.get(entity);
    }

    public Set<BehaviourComponent> getBehaviourComponentsOfEntity(Entity entity) {
        return behaviourComponentsPerEntity.get(entity);
    }

    /**
     * Gets the number of components of the entity.
     * 
     * @param entity The entity.
     *               
     * @return the number of components.
     */
    public int getNumberOfComponentsOfEntity(Entity entity) {
        int entityId = entity.getId();

        return signaturePerEntity.get(entityId).cardinality();
    }

    /**
     * Checks if the entity has the component.
     * 
     * @param entity The entity.
     * @param classType The component type class.
     * @param <T> The component type.
     *           
     * @return if it has the component.
     */
    public <T extends Component> boolean entityHasComponent(Entity entity, Class<T> classType) {
        int componentId = componentTypeRegistry.getIdForType(classType);
        int entityId = entity.getId();

        return signaturePerEntity.get(entityId).get(componentId);
    }

    /**
     * Removes the component from the entity.
     *
     * @param entity The entity.
     * @param classType The component type class.
     * @param <T> The component type.
     */
    public <T extends Component> void removeComponentFromEntity(Entity entity, Class<T> classType) {
        int componentId = componentTypeRegistry.getIdForType(classType);
        int entityId = entity.getId();

        T component = ((ComponentPool<T>)componentPools.get(componentId)).getFromEntity(entityId);
        component.destroy();

        componentsPerEntity.get(entityId).remove(component);
        ((ComponentPool<T>)componentPools.get(componentId)).remove(entityId);
        signaturePerEntity.get(entityId).clear(componentId);

        if (classType.isAssignableFrom(RenderableComponent.class)) {
            renderableComponentsPerEntity.get(entity).remove((RenderableComponent)component);
        }

        if (classType.isAssignableFrom(BehaviourComponent.class)) {
            behaviourComponentsPerEntity.get(entity).remove((BehaviourComponent)component);
        }

        if (Game.isDebugging()) {
            Out.printlnDebug("entity " + Style.Bold + entity.getId() + Style.Reset + ": component " +
                    Style.Bold + componentId + Style.Reset + ": removed");
        }

        entitiesToCheckStateForSystems.add(entity);
    }

    /**
     * Adds the entity to all systems.
     *
     * @param entity The entity.
     */
    public void addEntityToSystems(Entity entity) {
        int entityId = entity.getId();

        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            for (BitSet systemSignature : system.getValue().signatures) {
                if (systemSignature.get(componentTypeRegistry.getIdForType(BehaviourComponent.class)) && entityHasBehaviourComponent(entity) && !system.getValue().entities.contains(entity)) {
                    system.getValue().addEntity(entity);
                    break;
                }

                BitSet entitySignature = (BitSet) signaturePerEntity.get(entityId).clone();
                entitySignature.and(systemSignature);

                if (entitySignature.equals(systemSignature) && !system.getValue().entities.contains(entity)) {
                    system.getValue().addEntity(entity);
                    break;
                }
            }
        }
    }

    /**
     * Removes the entity from all systems.
     *
     * @param entity The entity.
     */
    public void removeEntityFromSystems(Entity entity) {
        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            system.getValue().removeEntity(entity);
        }
    }

    public <T extends System> void keepSystemOnLoad(Class<T> classType, boolean keep) {
        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            if (system.getValue().getClass() == classType) {
                if (keep) {
                    systemsToKeepBetweenLoads.add(system.getValue());
                } else {
                    systemsToKeepBetweenLoads.remove(system.getValue());
                }
            }
        }
    }

    public <T extends System> boolean isSystemKeptBetweenLoads(Class<T> classType) {
        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            if (system.getValue().getClass() == classType) {
                return systemsToKeepBetweenLoads.contains(system.getValue());
            }
        }

        return false;
    }

    /** Updates the registry (takes care of all recently created entities and all recently killed entities). */
    public void updateEntities() {
        addEntitiesToSystems();
        removeEntitiesFromEverything();
        removeEntitiesFromSystems();
        checkIfEntityShouldBeRemovedFromSystem();
    }

    /**
     * Calls update on every systems.
     *
     * @param dt The delta time.
     */
    public void update(float dt) {
        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            system.getValue().update(dt);
        }
    }

    /** Calls render on every systems. */
    public void render() {
        for (Map.Entry<Integer, System> system : systems.entrySet()) {
            system.getValue().render();
        }
    }

    /** @return the total number of entities currently living. */
    public int getTotalNumberOfEntities() {
        return totalNumberOfEntities;
    }

    /** @return the last entity number used. */
    public int getLastEntityNumber() {
        return lastEntityNumber;
    }

    /** @return a set of enabled entities. */
    public Set<Entity> getEnabledEntities() {
        return enabledEntities;
    }

    /** @return a set of disabled entities. */
    public Set<Entity> getDisabledEntities() {
        return disabledEntities;
    }

    public BitSet getSignatureOfEntity(Entity entity) {
        return signaturePerEntity.get(entity.getId());
    }

    /** @return the component type registry. */
    public ComponentTypeRegistry getComponentTypeRegistry() {
        return componentTypeRegistry;
    }

    /** @return an available ID. */
    public int requestId() {
        return freeIds.isEmpty() ? lastEntityNumber++ : freeIds.pop();
    }

    /** Add recently created or enabled entities to all systems. */
    private void addEntitiesToSystems() {
        for (Entity entity : entitiesToBeAddedToSystems) {
            addEntityToSystems(entity);
        }

        entitiesToBeAddedToSystems.clear();
    }

    /** Clears the registry (kills all entity, except entities to be kept between scenes). */
    public void clearRegistry() {
        Map<Integer, Entity> entitiesClone = new HashMap<>(entityPerIndex);

        for (Map.Entry<Integer, Entity> set : entitiesClone.entrySet()) {
            if (!entitiesToKeepBetweenLoads.contains(set.getValue())) {
                killEntity(set.getValue());
            }
        }

        updateEntities();

        Map<Integer, System> systemsClone = new HashMap<>(systems);

        for (Map.Entry<Integer, System> set : systemsClone.entrySet()) {
            if (!systemsToKeepBetweenLoads.contains(set.getValue())) {
                removeSystem(set.getValue().getClass());
            }
        }

        updateEntities();
    }

    /** Add all kept entities to system check to verify if they should be added to the systems added between scenes. */
    public void addAllKeptEntitiesToSystemCheck() {
        entitiesToBeAddedToSystems.addAll(entitiesToKeepBetweenLoads);
    }

    /** Remove all entities that are disabled from systems. */
    private void removeEntitiesFromSystems() {
        for (Entity entity : entitiesToBeRemovedFromSystems) {
            removeEntityFromSystems(entity);
        }

        entitiesToBeRemovedFromSystems.clear();
    }

    /** Removes recently killed entities from everything. */
    private void removeEntitiesFromEverything() {
        for (Entity entity : entitiesToBeRemovedFromEverything) {
            removeEntityFromSystems(entity);

            int entityId = entity.getId();

            Set<Component> copy = new HashSet<>(componentsPerEntity.get(entityId));

            for (Component component : copy) {
                removeComponentFromEntity(entity, component.getClass());
            }

            signaturePerEntity.remove(entityId);
            parentPerEntity.remove(entityId);
            childrenPerEntity.remove(entityId);
            entitiesAtRoot.remove(entity);
            entityPerIndex.remove(entityId);
            enabledEntities.remove(entity);
            disabledEntities.remove(entity);
            componentsPerEntity.remove(entityId);
            renderableComponentsPerEntity.remove(entity);
            behaviourComponentsPerEntity.remove(entity);
            entitiesToKeepBetweenLoads.remove(entity);

            freeIds.add(entityId);
            removeNameFromEntity(entity);
            removeAllTagsFromEntity(entity);
        }

        entitiesToBeRemovedFromEverything.clear();
    }

    /** Checks if some entities needs to be removed from the systems (will check their components). */
    private void checkIfEntityShouldBeRemovedFromSystem() {
        for (Entity entity : entitiesToCheckStateForSystems) {
            for (Map.Entry<Integer, System> system : systems.entrySet()) {
                if (system.getValue().entities.contains(entity)) {
                    boolean signatureFound = false;

                    for (BitSet systemSignature : system.getValue().signatures) {
                        if (systemSignature.get(componentTypeRegistry.getIdForType(BehaviourComponent.class)) && entityHasBehaviourComponent(entity)) {
                            signatureFound = true;
                            break;
                        }

                        BitSet entitySignature = (BitSet)entity.getSignature().clone();
                        entitySignature.and(systemSignature);

                        if (entitySignature.equals(systemSignature)) {
                            signatureFound = true;
                            break;
                        }
                    }

                    if (!signatureFound) {
                        system.getValue().entities.remove(entity);
                    }
                }
            }
        }

        entitiesToCheckStateForSystems.clear();
    }

    /**
     * Checks if the entity has at least one BehaviourComponent.
     *
     * @param entity The entity.
     * @return if it has a BehaviourComponent.
     */
    private boolean entityHasBehaviourComponent(Entity entity) {
        for (Component component : entity.getComponents()) {
            if (component instanceof BehaviourComponent) {
                return true;
            }
        }

        return false;
    }
}
