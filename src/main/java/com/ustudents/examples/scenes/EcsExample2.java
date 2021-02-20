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
import com.ustudents.engine.utility.SeedRandom;
import imgui.ImGui;
import imgui.flag.ImGuiCond;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class EcsExample2 extends Scene {
    Texture texture;

    @Override
    public void initialize() {
        // Permet de définir le zoom de la caméra (la distance est en world coordinates).
        camera.reload(100);

        // Charge une texture.
        texture = Resources.loadTexture("examples/grass.png");

        // Crée un générateur de nombre aléatoire (avec support de seed, mais pas important pour l'exemple).
        SeedRandom random = new SeedRandom();

        // On crée 100 entités.
        for (int x = 0; x < 10; x++) {
            for (int y = 0; y < 10; y++) {
                // Crée une nouvelle entité.
                Entity grass = registry.createEntity();

                // Je rajoute un TransformComponent à cette entité.
                // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de TransformComponent.
                grass.addComponent(TransformComponent.class, new Vector2f(x * 24, y * 24), new Vector2f(1, 1));

                // Je rajoute un SpriteComponent à cette entité.
                // Pour les arguments à appeler, ils doivent correspondre à un des constructeurs de SpriteComponent.
                // La texture utilisé contient 25 sprite différentes de grass sur une seule et même texture,
                // alors on choisis aléatoirement quel spirte utilisé en changeant le "texture region" voulu.
                int textureRegionX = 24 * random.generateInRange(1, 5);
                int textureRegionY = 24 * random.generateInRange(1, 5);
                grass.addComponent(TextureComponent.class, texture,
                        new Vector4f(textureRegionX, textureRegionY, 24, 24));

                // Je rajoute un RenderableComponent pour rendre l'entité affichable à l'écran.
                grass.addComponent(RendererComponent.class);
            }
        }
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
        ImGui.begin("Ecs Example 2");
        if (ImGui.button("Ecs Example 1")) {
            Game.get().getSceneManager().changeScene(EcsExample1.class);
        }
        ImGui.end();
    }

    @Override
    public void destroy() {
        texture.destroy();
    }
}
