package com.ustudents.farmland.entities.properties;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.TextureComponent;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.entities.Property;
import org.joml.Vector4f;

public class Field extends Property {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Field(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init() {
        addComponent(TextureComponent.class, Resources.loadTexture("Property/crops.png"),new Vector4f(0, 8 * 32, 32, 64));
    }

    public void init(SeedRandom random) {
        int textureRegionX = 32 * random.generateInRange(0, 16);
        addComponent(TextureComponent.class, Resources.loadTexture("Property/crops.png"),new Vector4f(textureRegionX, 8 * 32, 32, 64));
    }
}
