package io.github.dmitriyutkin.tgbotstarter.aop;

import io.github.dmitriyutkin.tgbotstarter.anotation.LogPerformanceSamplerAspect;
import io.github.dmitriyutkin.tgbotstarter.anotation.LoggableAspect;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class LogAspectImpl {

    @Before("@annotation(loggableAspect)")
    public void logMethod(JoinPoint joinPoint, LoggableAspect loggableAspect) {
        String classSimpleName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String message = String.format("Loggable | %s %s#%s: args: %s",
                                       loggableAspect.type().name(),
                                       classSimpleName,
                                       methodName,
                                       Arrays.toString(args));
        switch (loggableAspect.level()) {
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
            case DEBUG -> log.debug(message);
            case TRACE -> log.trace(message);
        }
    }

    @Around("@annotation(logPerformanceSamplerAspect)")
    public Object measureExecutionTime(ProceedingJoinPoint joinPoint, LogPerformanceSamplerAspect logPerformanceSamplerAspect) throws Throwable {
        String classSimpleName = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        StopWatch stowWatch = new StopWatch();

        stowWatch.start();
        Object proceed = joinPoint.proceed();
        stowWatch.stop();

        log.info("PerformanceSampler | {}#{} - {} ms", classSimpleName, methodName, stowWatch.getTotalTimeMillis());
        return proceed;
    }
}
