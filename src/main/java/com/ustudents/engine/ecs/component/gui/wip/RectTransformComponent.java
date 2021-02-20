package com.ustudents.engine.ecs.component.gui.wip;

import com.ustudents.engine.core.event.EventDispatcher;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

public class RectTransformComponent extends Component {
    @Viewable
    public TransformComponent base;

    @Viewable
    public Vector2f size;

    @Viewable
    public Anchor anchor;

    public EventDispatcher sizeChanged = new EventDispatcher();

    public RectTransformComponent() {
        this.base = new TransformComponent();
        this.size = new Vector2f();
        this.anchor = new Anchor();
    }

    public RectTransformComponent(TransformComponent base, Vector2f size, Anchor anchor) {
        this.base = base;
        this.size = size;
        this.anchor = anchor;
    }
}
