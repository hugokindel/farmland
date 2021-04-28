package com.ustudents.engine.ecs.component.graphic;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.ecs.component.core.TransformComponent;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.Spritebatch;
import com.ustudents.engine.graphic.Texture;
import com.ustudents.engine.graphic.imgui.annotation.Viewable;
import org.joml.Vector2f;
import org.joml.Vector4f;

/** Component for a texture to show on screen. */
@Viewable
public class TextureComponent extends Component implements RenderableComponent {
    /** The texture. */
    @Viewable
    public Texture texture;

    /** The color tint to apply on the texture. */
    @Viewable
    public Color tint;

    /**
     * The texture region.
     * - x,y contains the position to show within the texture.
     * - z,w contains the length to show after this position.
     */
    @Viewable
    public Vector4f region;

    /** The texture origin. */
    @Viewable
    public Vector2f origin;

    /**
     * Class constructor.
     *
     * @param texture The texture.
     */
    public TextureComponent(Texture texture) {
        this.texture = texture;
        this.region = new Vector4f(0, 0, texture.getWidth(), texture.getHeight());
        this.tint = Color.WHITE;
        this.origin = new Vector2f(0.0f, 0.0f);
    }

    /**
     * Sets the texture.
     *
     * @param texture The new texture.
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Sets the tint color.
     *
     * @param tint The new tint color.
     */
    public void setTint(Color tint) {
        this.tint = tint;
    }

    /**
     * Sets the texture region.
     *
     * @param region The new texture region.
     */
    public void setRegion(Vector4f region) {
        this.region = region;
    }

    /**
     * Sets the texture origin.
     *
     * @param origin The new texture origin.
     */
    public void setOrigin(Vector2f origin) {
        this.origin = origin;
    }

    @Override
    public void render(Spritebatch spritebatch, RendererComponent rendererComponent,
                       TransformComponent transformComponent) {


        Spritebatch.TextureData textureData = new Spritebatch.TextureData(texture, transformComponent.position);
        textureData.region = region;
        textureData.zIndex = rendererComponent.zIndex;
        textureData.tint = tint;
        textureData.rotation = transformComponent.rotation;
        textureData.scale = transformComponent.scale;
        textureData.origin = origin;

        spritebatch.drawTexture(textureData);
    }
}
