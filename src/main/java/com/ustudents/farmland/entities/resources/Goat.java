package com.ustudents.farmland.entities.resources;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.entities.Resource;
import org.joml.Vector4f;

public class Goat extends Resource {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Goat(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init() {
        addComponent(TextureComponent.class, Resources.loadTexture("Resource/goat.png"),new Vector4f(3 * 128, 5 * 128, 128, 128));
    }

    public void init(SeedRandom random){
        int textureRegionX = 128 * random.generateInRange(0, 3);
        addComponent(TextureComponent.class, Resources.loadTexture("Resource/goat.png"),new Vector4f(textureRegionX, (random.generateInRange(0, 1) == 0)? 5 * 128 : 7 * 128, 128, 128));
    }
}
