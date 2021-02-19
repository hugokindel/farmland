package com.ustudents.engine.utility;

import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;
import static java.lang.Math.sin;

/** Utility functions for mathematics. */
public class MathUtil {
    /** A cache of circle points (to provide faster result for drawing circles on the screen). */
    public static final Map<String, List<Vector2f>> circleCache = new HashMap<>();

    /**
     * Rotates a position with the given rotation at a specific origin.
     *
     * @param position The position.
     * @param origin The origin.
     * @param angle The rotation angle (in degrees).
     *
     * @return the rotated position.
     */
    public static Vector2f rotatePosition(Vector2f position, Vector2f origin, float angle) {
        double angleInRad = toRadians(angle);
        Vector2f translation = new Vector2f(position.x - origin.x, position.y - origin.y);
        Vector2f rotation = new Vector2f(
                (float)(translation.x * cos(angleInRad) - translation.y * sin(angleInRad)),
                (float)(translation.x * sin(angleInRad) + translation.y * cos(angleInRad))
        );

        return new Vector2f(rotation.x + origin.x, rotation.y + origin.y);
    }

    /**
     * Creates a list of positions to draw a circle of a specific radius with a specific number of sides.
     *
     * @param radius The radius.
     * @param sides The number of sides (which will define the siz of the list of positions).
     *
     * @return the list of positions to define a circle.
     */
    public static List<Vector2f> createCircle(float radius, int sides) {
        String circleKey = radius + "x" + sides;

        if (circleCache.containsKey(circleKey)) {
            return circleCache.get(circleKey);
        }

        List<Vector2f> vectors = new ArrayList<>();
        double max = 2.0 * PI;
        double step = max / sides;

        for (double theta = 0.0f; theta < max; theta += step) {
            vectors.add(new Vector2f((float)(radius * cos(theta)), (float)(radius * sin(theta))));
        }

        vectors.add(new Vector2f((float)(radius * cos(0)), (float)(radius * sin(0))));

        circleCache.put(circleKey, vectors);

        return vectors;
    }
}
