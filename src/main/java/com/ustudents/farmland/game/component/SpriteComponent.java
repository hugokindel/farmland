package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;
import com.ustudents.farmland.graphics.tools.annotation.Editable;

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
