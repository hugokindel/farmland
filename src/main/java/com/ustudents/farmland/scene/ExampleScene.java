package com.ustudents.farmland.scene;

import com.ustudents.engine.Game;
import com.ustudents.engine.audio.Sound;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.component.*;
import com.ustudents.engine.graphic.Font;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.farmland.component.MoveBlockComponent;
import com.ustudents.farmland.component.RotateBlockComponent;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;

public class ExampleScene extends Scene {
    Texture texture;
    Texture texture2;
    Font font;
    Sound sound;

    @Override
    public void initialize() {
        Entity playerContainer = registry.createEntity();
        playerContainer.setName("playerContainer");

        texture = Resources.loadTexture("examples/grass.png");
        texture2 = Resources.loadTexture("examples/lwjgl3.jpg");
        font = Resources.loadFont("default.ttf", 24);
        sound = Resources.loadSound("background.ogg");

        Entity player1 = registry.createEntity();
        player1.addComponent(TransformComponent.class, new Vector2f(0, 0), new Vector2f(1, 1));
        player1.addComponent(RenderableComponent.class);
        player1.addComponent(TextureComponent.class, texture);
        player1.addTag("players");
        player1.setName("player1");
        player1.setParent(playerContainer);

        Entity player2 = registry.createEntity();
        player2.addComponent(TransformComponent.class, new Vector2f(400, 400), new Vector2f(1, 1), 21.0f);
        player2.addComponent(RenderableComponent.class);
        player2.addComponent(TextureComponent.class, texture);
        player2.addTag("players");
        player2.setName("player2");
        player2.setParent(playerContainer);

        Entity player3 = registry.createEntity();
        player3.addComponent(TransformComponent.class, new Vector2f(-400, -400), new Vector2f(1, 1), 57.0f);
        player3.addComponent(RenderableComponent.class);
        player3.addComponent(TextureComponent.class, texture2);
        player3.addComponent(MoveBlockComponent.class);
        player3.addComponent(RotateBlockComponent.class);
        player3.addTag("players");
        player3.setName("player3");
        player3.setParent(playerContainer);

        Entity player4 = registry.createEntity();
        player4.addComponent(TransformComponent.class, new Vector2f(400, 0), new Vector2f(1, 1));
        player4.addComponent(RenderableComponent.class);
        registry.updateEntities();
        player4.addComponent(TextureComponent.class, texture2);
        player4.addTag("players");
        player4.setName("player4");
        player4.setParent(playerContainer);
        player1.getComponents();

        Entity playerName = registry.createEntity();
        playerName.addComponent(TransformComponent.class);
        playerName.addComponent(RenderableComponent.class);
        playerName.addComponent(TextComponent.class, "forx", font);

        Entity music = registry.createEntity();
        music.setName("music");
        music.addComponent(SoundComponent.class, sound, true);
    }

    @Override
    public void update(float dt) {

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
