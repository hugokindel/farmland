package com.ustudents.farmland.map;

import com.ustudents.engine.core.cli.print.Out;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.input.Input;
import org.joml.Vector4f;

public class CellComponent extends BehaviourComponent {
    @Override
    public void update(float dt) {
        if (Input.isMouseInWorldViewRect(getViewRect())) {
            getRegistry().getEntityByName("cellCursor").getComponent(TransformComponent.class).setPosition(getEntity().getComponent(TransformComponent.class).position);
        }
    }

    private Vector4f getViewRect() {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        TextureComponent texture = getEntity().getComponent(TextureComponent.class);
        return new Vector4f(transform.position.x, transform.position.y, transform.position.x + texture.region.z, transform.position.y + texture.region.w);
    }
}
