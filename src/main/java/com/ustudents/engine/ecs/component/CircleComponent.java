package com.ustudents.engine.ecs.component;

import com.ustudents.engine.core.json.annotation.JsonSerializable;
import com.ustudents.engine.ecs.Component;
import com.ustudents.engine.graphic.Color;
import com.ustudents.engine.graphic.imgui.annotation.Editable;

@Editable
@JsonSerializable
public class CircleComponent extends Component {
    /** The radius. */
    @JsonSerializable
    @Editable
    public Float radius;

    /** The number of sides to render. */
    @JsonSerializable
    @Editable
    public Integer sides;

    /** The color. */
    @JsonSerializable
    @Editable
    public Color color;

    /** The thickness of the lines. */
    @JsonSerializable
    @Editable
    public Integer thickness;

    public CircleComponent() {
        this(null, null, null, null);
    }

    /**
     * Class constructor.
     *
     * @param radius The radius.
     * @param sides The sides.
     */
    public CircleComponent(Float radius, Integer sides) {
        this(radius, sides, Color.WHITE, 1);
    }

    /**
     * Class constructor.
     *
     * @param radius The radius.
     * @param sides The sides.
     * @param color The color.
     */
    public CircleComponent(Float radius, Integer sides, Color color) {
        this(radius, sides, color, 1);
    }

    /**
     * Class constructor.
     *
     * @param radius The radius.
     * @param sides The sides.
     * @param color The color.
     * @param thickness The thickness.
     */
    public CircleComponent(Float radius, Integer sides, Color color, Integer thickness) {
        this.radius = radius;
        this.sides = sides;
        this.color = color;
        this.thickness = thickness;
    }
}
