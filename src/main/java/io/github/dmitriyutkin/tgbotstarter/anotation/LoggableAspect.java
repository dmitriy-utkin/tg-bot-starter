package io.github.dmitriyutkin.tgbotstarter.anotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoggableAspect {
    LoggableType type() default LoggableType.OTHER;
    LoggableLevelType level() default LoggableLevelType.INFO;
}
