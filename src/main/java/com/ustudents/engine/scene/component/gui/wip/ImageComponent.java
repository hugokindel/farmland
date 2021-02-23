package com.ustudents.engine.scene.component.gui.wip;

import com.ustudents.engine.scene.component.graphics.TextureComponent;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;

public class ImageComponent extends GuiComponent {
    @Viewable
    public TextureComponent texture;

    public ImageComponent(TextureComponent texture) {

    }
}
