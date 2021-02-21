package com.ustudents.farmland.component;

import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.input.Input;
import org.joml.Vector4f;

public class CellComponent extends BehaviourComponent {
    private Vector4f viewRect;

    @Override
    public void initialize() {
        super.initialize();

        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        TextureComponent texture = getEntity().getComponent(TextureComponent.class);
        viewRect = new Vector4f(transform.position.x, transform.position.y, transform.position.x + texture.region.z, transform.position.y + texture.region.w);
    }

    @Override
    public void update(float dt) {
        if (Input.isMouseInWorldViewRect(viewRect)) {
            getRegistry().getEntityByName("cellCursor").getComponent(TransformComponent.class).setPosition(getEntity().getComponent(TransformComponent.class).position);
        }
    }
}
