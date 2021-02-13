package com.ustudents.examples.scenes;

import com.ustudents.engine.Game;
import com.ustudents.engine.core.Resources;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.SpriteComponent;
import com.ustudents.engine.ecs.component.TransformComponent;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.ImGuiUtils;
import com.ustudents.engine.scene.Scene;
import com.ustudents.engine.utility.SeedRandom;
import com.ustudents.farmland.entities.mapElements.*;
import com.ustudents.farmland.scene.MainMenu;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector4i;

public class EcsExample3 extends Scene {

    @Override
    public void initialize() {
        camera.reload(100);

        SeedRandom random = new SeedRandom();

        for (int x = -10; x < 10; x++) {
            for (int y = -10; y < 10; y++) {
                int rd = random.generateInRange(0, 20);
                if(rd < 4){
                    Mountain mountain = registry.createEntity(Mountain.class);
                    mountain.init(random);
                    mountain.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));
                }
                Plain grass = registry.createEntity(Plain.class);
                grass.init(random);
                grass.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));

            }
        }
    }

    @Override
    public void update(double dt) {

    }

    @Override
    public void render() {

    }

    @Override
    public void renderImGui() {
        ImGuiUtils.setNextWindowWithSizeCentered(300, 300, ImGuiCond.Appearing);
        ImGui.begin("Ecs Example 3");
        if (ImGui.button("Ecs Example 1")) {
            Game.get().getSceneManager().changeScene(EcsExample1.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {

    }
}