package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.core.json.annotation.JsonSerializableConstructor;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;
import org.joml.Vector2f;

import static com.ustudents.engine.core.Resources.getTexturesDirectory;

@JsonSerializable
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
    @JsonSerializable
    @Editable
    public Float length;

    /** The color. */
    @JsonSerializable
    @Editable
    public Color color;

    /** The thickness. */
    @JsonSerializable
    @Editable
    public Integer thickness;

    /** The point. */
    @JsonSerializable
    @Editable
    public Vector2f point2;

    /** The type to use. */

    @Editable
    public Type type;

    public LineComponent() {
        this(null, null, null);
    }

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
        this.point2 = null;
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
        this.length = null;
        this.type = Type.FromPoint;
    }

    @JsonSerializableConstructor
    private void fromJson() {
        if (point2 != null) {
            this.type = Type.FromPoint;
        } else {
            this.type = Type.FromLength;
        }
    }
}
