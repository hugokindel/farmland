package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.component.TextComponent;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.ecs.component.TransformComponent;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;

public class ExampleScene extends Scene {
    Entity player3;
    Texture texture;
    Texture texture2;
    Font font;

    @Override
    public void initialize() {
        Entity playerContainer = registry.createEntity();
        playerContainer.setName("playerContainer");

        texture = Resources.loadTexture("examples/grass.png");
        texture2 = Resources.loadTexture("examples/lwjgl3.jpg");
        font = Resources.loadFont("default.ttf", 24);

        Entity player1 = registry.createEntity();
        player1.addComponent(TransformComponent.class, new Vector2f(0, 0), new Vector2f(1, 1));
        player1.addComponent(SpriteComponent.class, texture);
        player1.addTag("players");
        player1.setName("player1");
        player1.setParent(playerContainer);

        Entity player2 = registry.createEntity();
        player2.addComponent(TransformComponent.class, new Vector2f(400, 400), new Vector2f(1, 1), 21.0f);
        player2.addComponent(SpriteComponent.class, texture);
        player2.addTag("players");
        player2.setName("player2");
        player2.setParent(playerContainer);

        player3 = registry.createEntity();
        player3.addComponent(TransformComponent.class, new Vector2f(-400, -400), new Vector2f(1, 1), 57.0f);
        player3.addComponent(SpriteComponent.class, texture2);
        player3.addTag("players");
        player3.setName("player3");
        player3.setParent(playerContainer);

        Entity player4 = registry.createEntity();
        player4.addComponent(TransformComponent.class, new Vector2f(400, 0), new Vector2f(1, 1));
        player4.addComponent(SpriteComponent.class, texture2);
        player4.addTag("players");
        player4.setName("player4");
        player4.setParent(playerContainer);
        player1.getComponents();

        Entity playerName = registry.createEntity();
        playerName.addComponent(TransformComponent.class);
        playerName.addComponent(TextComponent.class, "forx", font);
    }

    @Override
    public void update(double dt) {
        player3.getComponent(TransformComponent.class).position.x += 30 * dt;

        int scroll = Input.scroll();
        if(scroll != 0 && getCamera().getZoom() <= 2000 && getCamera().getZoom() >= 50) {
            camera.reload(camera.getZoom() - 50 * scroll);
            camera.setSize(1280, 720);
        }
    }

    @Override
    public void render() {
        /*spriteBatch.begin();
        spriteBatch.draw(texture, new Vector4i(0, 0, 400, 400), new Vector2f(100, 100), 1);
        spriteBatch.draw(texture, new Vector4i(0, 0, 400, 400), new Vector2f(500, 500), 1);
        spriteBatch.draw(texture2, new Vector4i(0, 0, 400, 400), new Vector2f(pos, 0), 1);
        spriteBatch.end();*/
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Example Scene");
        if (ImGui.button("Main Menu")) {
            Game.get().getSceneManager().changeScene(MainMenu.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy()
    {

    }
}
