package io.github.dmitriyutkin.tgbotstarter.aop;

import io.github.dmitriyutkin.tgbotstarter.aop.props.LoggableLevelType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LogPerformanceSamplerAspect {
    LoggableLevelType level() default LoggableLevelType.DEBUG;
}
