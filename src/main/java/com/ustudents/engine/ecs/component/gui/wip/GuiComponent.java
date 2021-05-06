package com.ustudents.engine.ecs.component.gui.wip;

import com.ustudents.engine.core.event.Event;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.Entity;
import com.ustudents.engine.ecs.component.core.BehaviourComponent;
import com.ustudents.engine.graphic.Anchor;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;

public class GuiComponent extends BehaviourComponent {
    public class SizeChangedEvent extends Event {
        public Vector2f newSize;

        public SizeChangedEvent(Vector2f newSize) {
            this.newSize = newSize;
        }
    }

    @Viewable
    public Vector2f size;

    @Viewable
    public Anchor anchor;

    @Override
    public void initialize() {
        getCanvas();
        getRect();
    }

    protected GuiComponent getParent() {
        Entity entity = getEntity();

        if (entity.getParent() != null) {
            for (Component component : entity.getComponents()) {
                if (component instanceof GuiComponent) {
                    return (GuiComponent)component;
                }
            }
        }

        return null;
    }

    protected RectTransformComponent getRect() {
        Entity entity = getEntity();

        if (entity.hasComponent(RectTransformComponent.class)) {
            return entity.getComponent(RectTransformComponent.class);
        }

        //Out.printlnError("Any GUI component must have RectTransformComponent among his family tree!");

        return null;
    }

    protected CanvasComponent getCanvas() {
        Entity entity = getEntity();

        while (entity != null) {
            if (entity.hasComponent(CanvasComponent.class)) {
                return entity.getComponent(CanvasComponent.class);
            }

            entity = entity.getParent();
        }

        //Out.printlnError("Any GUI component must have a " + Style.Bold + "CanvasComponent" + Style.Reset + " among his family tree!");

        return null;
    }
}
