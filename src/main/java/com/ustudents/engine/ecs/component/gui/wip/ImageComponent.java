package com.ustudents.engine.ecs.component.gui.wip;

import com.ustudents.engine.ecs.component.graphic.TextureComponent;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;

public class ImageComponent extends GuiComponent {
    @Viewable
    public TextureComponent texture;

    public ImageComponent(TextureComponent texture) {

    }
}
