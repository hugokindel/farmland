package com.ustudents.engine.utility;

/** A container for a pair of objects. */
@SuppressWarnings({"unused"})
public class Pair<T, U> {
    /** The first object of the pair. */
    private T object1;

    /** The second object of the pair. */
    private U object2;

    /**
     * Class constructor.
     *
     * @param object1 The first object.
     * @param object2 The second object.
     */
    public Pair(T object1, U object2) {
        this.object1 = object1;
        this.object2 = object2;
    }

    /** @return the first object of the pair. */
    public T getObject1() {
        return object1;
    }

    /** @return the second object of the pair. */
    public U getObject2() {
        return object2;
    }

    /**
     * Sets the first object of the pair.
     *
     * @param object The value.
     */
    public void setObject1(T object) {
        object1 = object;
    }

    /**
     * Sets the second object of the pair.
     *
     * @param object The value.
     */
    public void setObject2(U object) {
        object2 = object;
    }
}
