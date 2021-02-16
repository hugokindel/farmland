package com.ustudents.engine.core.json.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Annotation interface for a Json serializable type's constructor. */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonSerializableConstructor {

}