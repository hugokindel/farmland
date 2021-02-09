package com.ustudents.farmland.scene;

import com.ustudents.farmland.Farmland;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.input.Input;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.scene.ecs.Entity;
import com.ustudents.farmland.component.BoxColliderComponent;
import com.ustudents.farmland.component.CameraFollowComponent;
import com.ustudents.farmland.component.SpriteComponent;
import com.ustudents.farmland.component.TransformComponent;
import com.ustudents.engine.graphics.Shader;
import com.ustudents.engine.graphics.SpriteBatch;
import com.ustudents.engine.graphics.Texture;
import com.ustudents.engine.graphics.imgui.ImGuiUtils;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector4i;

public class ExampleScene extends Scene {
    @Override
    public void initialize() {
        Entity playerContainer = registry.createEntity();
        playerContainer.setName("playerContainer");

        Entity player1 = registry.createEntity();
        player1.addComponent(TransformComponent.class, new Vector2f(57, 52), new Vector2f(1, 1));
        player1.addComponent(SpriteComponent.class, "player1.png");
        player1.addComponent(CameraFollowComponent.class);
        player1.addComponent(BoxColliderComponent.class, 50, 50);
        player1.addTag("players");
        player1.setName("player1");
        player1.setParent(playerContainer);

        Entity player2 = registry.createEntity();
        player2.addComponent(TransformComponent.class, new Vector2f(12, 67), new Vector2f(1, 1), 21.0f);
        player2.addComponent(SpriteComponent.class, "player2.png");
        player2.addComponent(BoxColliderComponent.class, 50, 50);
        player2.addTag("players");
        player2.setName("player2");
        player2.setParent(playerContainer);

        Entity player3 = registry.createEntity();
        player3.addComponent(TransformComponent.class, new Vector2f(876, 20), new Vector2f(1, 1), 57.0f);
        player3.addComponent(SpriteComponent.class, "player3.png");
        player3.addComponent(BoxColliderComponent.class, 75, 75);
        player3.addTag("players");
        player3.setName("player3");
        player3.setParent(playerContainer);

        Entity player4 = registry.createEntity();
        player4.addComponent(TransformComponent.class, new Vector2f(78, 1), new Vector2f(1, 1));
        player4.addComponent(SpriteComponent.class, "player4.png");
        player4.addComponent(BoxColliderComponent.class, 80, 80);
        player4.addTag("players");
        player4.setName("player4");
        player4.setParent(playerContainer);
        player1.getComponents();

        shader = Resources.loadShader("spritebatch");
        texture = Resources.loadTexture("forx.png");
        texture2 = Resources.loadTexture("lwjgl.jpg");
        spriteBatch = new SpriteBatch();
    }

    Shader shader;
    Texture texture;
    Texture texture2;
    SpriteBatch spriteBatch;
    float pos = 0.0f;

    @Override
    public void update(double dt) {
        pos += 30 * dt;

        int scroll = Input.scroll();

        if(scroll != 0 && getCamera().getZoom()<=2000
                && getCamera().getZoom()>=50){
            camera.reload(camera.getZoom() - 50*scroll);
            camera.setSize(1280, 720);
        }
    }

    @Override
    public void render() {
        spriteBatch.begin();
        spriteBatch.draw(texture, new Vector4i(0, 0, 400, 400), 100, 100, 1);
        spriteBatch.draw(texture, new Vector4i(0, 0, 400, 400), 500, 500, 1);
        spriteBatch.draw(texture2, new Vector4i(0, 0, 400, 400), pos, 0, 1);
        spriteBatch.end();
    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Example Scene");

        if (ImGui.button("Main Menu")) {
            Farmland.get().getSceneManager().changeScene(MainMenu.class);
        }

        ImGui.end();
    }

    @Override
    public void destroy()
    {
        shader.destroy();
        texture.destroy();
    }
}
