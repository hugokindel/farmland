package com.ustudents.engine.tools.console;

import com.ustudents.engine.network.NetMode;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConsoleCommand {
    /** Name of the command. */
    String name() default "";

    /** Description of the command. */
    String description() default "";

    /** In which mode can this command be run. */
    NetMode[] authority() default {NetMode.Standalone, NetMode.Client, NetMode.ListenServer, NetMode.DedicatedServer};

    String[] argsDescription() default "";
}
