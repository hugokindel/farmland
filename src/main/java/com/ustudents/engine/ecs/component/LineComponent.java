package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

import static com.ustudents.engine.core.Resources.getTexturesDirectory;

@Editable
public class LineComponent extends Component {
    /** The color. */
    @Editable
    public Color color;

    /** The thickness. */
    @Editable
    public Integer thickness;

    /** The point. */
    @Editable
    public Vector2f point2;

    public LineComponent() {
        this(null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param point2 The point.
     */
    public LineComponent(Vector2f point2) {
        this(point2, Color.WHITE, 1);
    }

    /**
     * Class constructor.
     *
     * @param point2 The point.
     * @param color The color.
     */
    public LineComponent(Vector2f point2, Color color) {
        this(point2, color, 1);
    }

    /**
     * Class constructor.
     *
     * @param point2 The point.
     * @param color The color.
     * @param thickness The thickness.
     */
    public LineComponent(Vector2f point2, Color color, Integer thickness) {
        this.point2 = point2;
        this.color = color;
        this.thickness = thickness;
    }
}
