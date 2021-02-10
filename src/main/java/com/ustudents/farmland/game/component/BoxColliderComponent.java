package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;
import com.ustudents.farmland.graphics.tools.annotation.Editable;

public class BoxColliderComponent extends Component {
    @Editable
    public Integer width;

    @Editable
    public Integer height;

    public BoxColliderComponent(Integer width, Integer height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public String toString() {
        return "BoxColliderComponent{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
