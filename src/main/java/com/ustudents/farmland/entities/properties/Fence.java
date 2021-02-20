package com.ustudents.farmland.entities.properties;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.farmland.entities.Property;
import org.joml.Vector4f;

public class Fence extends Property {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Fence(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init() {
        addComponent(TextureComponent.class, Resources.loadTexture("Property/fence.png"),new Vector4f(0, 0, 96, 32));
    }
}
