package com.ustudents.engine.core.console;

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
}
