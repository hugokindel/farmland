package com.ustudents.engine.ecs.component;

import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

public class LineComponent extends Component {
    /**
     * The type to use:
     * - FromLength: Will use length attribute.
     * - FromPoint: Will use point2 attribute.
     */
    public enum Type {
        FromLength,
        FromPoint
    }

    /** The length. */
    @Editable
    public Float length;

    /** The color. */
    @Editable
    public Color color;

    /** The thickness. */
    @Editable
    public Integer thickness;

    /** The point. */
    @Editable
    public Vector2f point2;

    /** The type to use. */
    @Editable
    public Type type;

    /**
     * Class constructor.
     *
     * @param length The length.
     */
    public LineComponent(float length) {
        this(
                length,
                Color.WHITE,
                1
        );
    }

    /**
     * Class constructor.
     *
     * @param length The length.
     * @param color The color.
     */
    public LineComponent(float length, Color color) {
        this(
                length,
                color,
                1
        );
    }

    /**
     * Class constructor.
     *
     * @param length The length.
     * @param color The color.
     * @param thickness The thickness.
     */
    public LineComponent(float length, Color color, Integer thickness) {
        this.length = length;
        this.color = color;
        this.thickness = thickness;
        this.point2 = new Vector2f(0.0f, 0.0f);
        this.type = Type.FromLength;
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
        this.length = 0.0f;
        this.type = Type.FromPoint;
    }
}
