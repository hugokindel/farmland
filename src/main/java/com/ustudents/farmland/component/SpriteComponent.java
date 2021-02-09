package com.ustudents.farmland.component;

import com.ustudents.engine.scene.ecs.Component;
import com.ustudents.engine.graphics.imgui.annotation.Editable;

public class SpriteComponent extends Component {
    @Editable
    public String textureName;

    public SpriteComponent(String textureName) {
        this.textureName = textureName;
    }

    @Override
    public String toString() {
        return "SpriteComponent{" +
                "textureName='" + textureName + '\'' +
                '}';
    }
}
