package com.ustudents.farmland.entities.mapElements;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Registry;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.farmland.entities.MapElement;
import org.joml.Vector4f;

public class Citie extends MapElement {
    /**
     * Class constructor.
     *
     * @param id       The ID.
     * @param registry The registry.
     */
    public Citie(Integer id, Registry registry) {
        super(id, registry);
    }

    @Override
    public void init() {
        addComponent(TextureComponent.class, Resources.loadTexture("MapElement/cities.png"),new Vector4f(0, 0, 28, 38));
    }
}
