package com.ustudents.farmland.game.component;

import com.ustudents.farmland.ecs.Component;

public class BoxColliderComponent extends Component {
    public Integer width;
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
