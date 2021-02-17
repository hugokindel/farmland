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
import com.ustudents.farmland.entities.properties.*;
import com.ustudents.farmland.entities.resources.*;
import com.ustudents.farmland.scene.MainMenu;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.joml.Vector4i;

public class EcsExample3 extends Scene {

    @Override
    public void initialize() {
        // Permet de définir le zoom de la caméra
        camera.reload(100);

        // Crée un générateur de nombre aléatoire
        SeedRandom random = new SeedRandom();

        // On crée des entités.
        for (int x = -10; x < 10; x++) {
            for (int y = -10; y < 10; y++) {
                int rd = random.generateInRange(0, 20);
                if(rd < 1){
                    Citie citie = registry.createEntity(Citie.class);
                    citie.init();
                    citie.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));
                }
                if(rd < 4){
                    // Crée une nouvelle entité.
                    Mountain mountain = registry.createEntity(Mountain.class);
                    // J'initialise l'entité (son SpriteComponent) avec le générateur de nombre aléatoire ( certaines entités ne peuvent pas etre initialisé avec )
                    mountain.init(random);
                    // Je rajoute un TransformComponent
                    mountain.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));
                }
                Plain grass = registry.createEntity(Plain.class);
                grass.init(random);
                grass.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));
                if(y == 0 && x%2 == 0){
                    Field field = registry.createEntity(Field.class);
                    field.init(random);
                    field.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }
                if(y == 3 && x%3 == 0){
                    Fence fence = registry.createEntity(Fence.class);
                    fence.init();
                    fence.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }
                if(y == 0 && x%2 == 0){
                    Corn corn = registry.createEntity(Corn.class);
                    corn.init();
                    corn.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24 - 6), new Vector2f(1,1));
                }
                if(y == -3 && x%2 == 0){
                    Pumpkin pumpkin = registry.createEntity(Pumpkin.class);
                    pumpkin.init();
                    pumpkin.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }
                if(y == -6 && x%2 == 0){
                    WaterMelon waterMelon = registry.createEntity(WaterMelon.class);
                    waterMelon.init();
                    waterMelon.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }if(y == 6 && x%2 == 0){
                    Pepper pepper = registry.createEntity(Pepper.class);
                    pepper.init();
                    pepper.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }
                if(y == 4 && x%2 == 0){
                    Carrots carrots = registry.createEntity(Carrots.class);
                    carrots.init();
                    carrots.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1,1));
                }

            }
        }
        Cow cow = registry.createEntity(Cow.class);
        cow.init(random);
        cow.addComponent(TransformComponent.class, new Vector2f(-128, 200), new Vector2f(1,1));
        Goat goat = registry.createEntity(Goat.class);
        goat.init(random);
        goat.addComponent(TransformComponent.class, new Vector2f(0, 200), new Vector2f(1,1));
        Pig pig = registry.createEntity(Pig.class);
        pig.init(random);
        pig.addComponent(TransformComponent.class, new Vector2f(128, 200), new Vector2f(1,1));
        Sheep sheep = registry.createEntity(Sheep.class);
        sheep.init(random);
        sheep.addComponent(TransformComponent.class, new Vector2f(-256, 200), new Vector2f(1,1));
        Chicken chicken = registry.createEntity(Chicken.class);
        chicken.init(random);
        chicken.addComponent(TransformComponent.class, new Vector2f(-384, 200), new Vector2f(1,1));
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