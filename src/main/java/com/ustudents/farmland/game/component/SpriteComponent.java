package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;

public class SpriteComponent extends Component {
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
