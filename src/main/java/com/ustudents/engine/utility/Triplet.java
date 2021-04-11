package com.ustudents.engine.utility;

import com.ustudents.engine.core.json.annotation.JsonSerializable;

/** A container for a triplet of objects. */
@SuppressWarnings({"unused"})
@JsonSerializable
public class Triplet<T, U, W> {
    /** The first object of the triplet. */
    @JsonSerializable
    private T object1;

    /** The second object of the triplet. */
    @JsonSerializable
    private U object2;

    /** The third object of the triplet. */
    @JsonSerializable
    private W object3;

    public Triplet() {

    }

    /**
     * Class constructor.
     *
     * @param object1 The first object.
     * @param object2 The second object.
     * @param object3 The third object.
     */
    public Triplet(T object1, U object2, W object3) {
        this.object1 = object1;
        this.object2 = object2;
        this.object3 = object3;
    }

    /** @return the first object of the triplet. */
    public T getObject1() {
        return object1;
    }

    /** @return the second object of the triplet. */
    public U getObject2() {
        return object2;
    }

    /** @return the third object of the triplet. */
    public W getObject3() {
        return object3;
    }

    /**
     * Sets the first object of the triplet.
     *
     * @param object The value.
     */
    public void setObject1(T object) {
        object1 = object;
    }

    /**
     * Sets the second object of the triplet.
     *
     * @param object The value.
     */
    public void setObject2(U object) {
        object2 = object;
    }

    /**
     * Sets the third object of the triplet.
     *
     * @param object The value.
     */
    public void setObject3(W object) {
        object3 = object;
    }

    @Override
    public String toString() {
        return "Triplet{" +
                "object1=" + object1 +
                ", object2=" + object2 +
                ", object3=" + object3 +
                '}';
    }
}