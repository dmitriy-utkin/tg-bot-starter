package io.github.dmitriyutkin.tgbotstarter.aop;

import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableLevelType;
import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableType;

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
