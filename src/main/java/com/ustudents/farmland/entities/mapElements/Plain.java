package com.ustudents.farmland.entities.mapElements;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.entities.MapElement;
import org.joml.Vector4f;

public class Plain extends MapElement {

    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Plain(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init(){
        addComponent(TextureComponent.class, Resources.loadTexture("MapElement/plain.png"),new Vector4f(1, 1, 24, 24));
    }

    public void init(SeedRandom random) {
        int textureRegionX = 24 * random.generateInRange(1, 5);
        int textureRegionY = 24 * random.generateInRange(1, 5);
        addComponent(TextureComponent.class, Resources.loadTexture("MapElement/plain.png"),new Vector4f(textureRegionX, textureRegionY, 24, 24));
    }
}
