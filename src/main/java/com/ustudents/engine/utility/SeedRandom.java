package com.ustudents.engine.utility;

import java.util.Random;

/** A random generator which can handle seeds to provide reproducible random values. */
public class SeedRandom {
    /** The seed. */
    private long seed;

    /** The random generator. */
    private Random random;

    /** Class constructor (uses a pretty random value as a seed). */
    public SeedRandom() {
        this(System.currentTimeMillis());
    }

    /**
     * Class constructor.
     *
     * @param seed The seed.
     */
    public SeedRandom(long seed) {
        setSeed(seed);
    }

    /** @return the seed. */
    public long getSeed() {
        return seed;
    }

    /**
     * Sets the seed and recreate the random generator.
     *
     * @param seed The seed.
     */
    public void setSeed(long seed) {
        this.seed = seed;
        random = new Random(seed);
    }

    /** @return the random generator. */
    public Random getRandom() {
        return random;
    }

    /** @return a random value between 0 and 2147483647 (Integer.MAX_VALUE). */
    public int generate() {
        return random.nextInt(Integer.MAX_VALUE);
    }

    /** Generates a random value between min and max (both inclusive). */
    public int generateInRange(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }

    /** Generates a random value between 0 and max (inclusive). */
    public int generateWithMaximum(int max) {
        return random.nextInt(max + 1);
    }
}
