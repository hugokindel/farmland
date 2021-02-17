package com.ustudents.farmland.entities.mapElements;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.entities.MapElement;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class Mountain extends MapElement {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Mountain(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init() {
        addComponent(SpriteComponent.class, Resources.loadTexture("MapElement/mountain.png"),new Vector4f(0, 0, 24, 24),1);
    }

    public void init(SeedRandom random) {
        int textureRegionX = 24 * random.generateInRange(0, 1);
        addComponent(SpriteComponent.class, Resources.loadTexture("MapElement/mountain.png"),new Vector4f(textureRegionX, 0, 24, 24),1);
    }
}
