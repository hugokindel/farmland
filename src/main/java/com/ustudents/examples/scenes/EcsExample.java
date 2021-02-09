package com.ustudents.examples.scenes;

import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.ecs.component.TransformComponent;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.scene.Scene;
import com.ustudents.farmland.component.BoxColliderComponent;
import com.ustudents.farmland.component.CameraFollowComponent;
import org.joml.Vector2f;

public class EcsExample extends Scene {
    Texture texture;

    @Override
    public void initialize() {
        // Charge une texture.
        texture = Resources.loadTexture("forx.png");

        // Crée une nouvelle entité.
        Entity player1 = registry.createEntity();

        // Je rajoute un TransformComponent à cette entité.
        // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de TransformComponent.
        player1.addComponent(TransformComponent.class, new Vector2f(0, 0), new Vector2f(1, 1));

        // Je rajoute un SpriteComponent à cette entité.
        // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de SpriteComponent.
        player1.addComponent(SpriteComponent.class, texture);
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void destroy() {

    }
}
