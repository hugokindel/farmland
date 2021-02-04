package com.ustudents.farmland;

/** This is main class of the program. */
public class Main {
    /** The instance of the project. */
    public static Farmland farmland;

    /** Start of the program. */
    public static void main(String[] args) {
        farmland = new Farmland();
        System.exit(farmland.run(args));
    }
}
