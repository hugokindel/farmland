package com.ustudents.examples.scenes;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.graphics.RendererComponent;
import com.ustudents.engine.ecs.component.graphics.TextureComponent;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;

public class EcsExample1 extends Scene {
    Texture texture;

    @Override
    public void initialize() {
        // Charge une texture.
        texture = Resources.loadTexture("examples/lwjgl3.jpg");

        // Crée une nouvelle entité.
        Entity player1 = registry.createEntity();

        // Je rajoute un TransformComponent à cette entité.
        // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de TransformComponent.
        player1.addComponent(TransformComponent.class, new Vector2f(0, 0), new Vector2f(1, 1));

        // Je rajoute un SpriteComponent à cette entité.
        // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de SpriteComponent.
        player1.addComponent(TextureComponent.class, texture);

        // Je rajoute un RenderableComponent pour rendre l'entité affichable à l'écran.
        player1.addComponent(RendererComponent.class);
    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui() {
        // Exemple de menu ImGui.
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Ecs Example 1");
        if (ImGui.button("Ecs Example 2")) {
            Game.get().getSceneManager().changeScene(EcsExample2.class);
        }
        if (ImGui.button("Ecs Example 3")) {
            Game.get().getSceneManager().changeScene(EcsExample3.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {
        texture.destroy();
    }
}
