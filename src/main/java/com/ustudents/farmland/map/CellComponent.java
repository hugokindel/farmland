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
            entity.getComponent(TextureComponent.class).tint = Color.RED;
        } else {
            entity.getComponent(TextureComponent.class).tint = Color.WHITE;
        }
    }

    private Vector4f getViewRect() {
        TransformComponent transform = entity.getComponent(TransformComponent.class);
        TextureComponent texture = entity.getComponent(TextureComponent.class);
        return new Vector4f(transform.position.x, transform.position.y, transform.position.x + texture.region.z, transform.position.y + texture.region.w);
    }
}
