package com.ustudents.engine.core.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation interface for a Json serializable type or field. */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSerializable {
    /** The path to search for this element. */
    String path() default "";

    /** Defines if this is a necessary field or not (if not, it can be deserialized as null if not found). */
    boolean necessary() default true;

    JsonSerializableType type() default JsonSerializableType.Both;
}