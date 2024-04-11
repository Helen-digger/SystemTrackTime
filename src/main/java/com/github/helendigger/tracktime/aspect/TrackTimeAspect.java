package com.github.helendigger.tracktime.aspect;

import com.github.helendigger.tracktime.service.TimeRecordService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.Optional;

@Component
@Aspect
@AllArgsConstructor
@Slf4j
public class TrackTimeAspect {
    private final TimeRecordService timeRecordService;
    @Pointcut("@annotation(com.github.helendigger.tracktime.aspect.annotation.TrackTime)")
    private void trackTimeBlockingAnnotation() {}

    @Pointcut("@annotation(com.github.helendigger.tracktime.aspect.annotation.TrackTimeAsync)")
    private void trackTimeAsyncAnnotation() {}

    @Pointcut("execution(* * (..))")
    private void executionPointcut() {}

    /**
     * This advice tracks time of an annotated method in a current thread of execution
     * @param joinPoint join point got from the pointcuts
     * @return returns advised method's return value
     * @throws Throwable throws exception if advised method did throw an exception
     * The time is tracked even if method finished execution exceptionally
     */
    @Around("trackTimeBlockingAnnotation() && executionPointcut()")
    private Object trackTimeAdviceBlocking(ProceedingJoinPoint joinPoint) throws Throwable {
        final var methodName = getMethodNameFromJoinPoint(joinPoint);
        final var className = getClassNameFromJoinPoint(joinPoint);
        final var packageName = getPackageNameFromJoinPoint(joinPoint);
        Instant start = Instant.now();
        var recordId = timeRecordService.createNewTimeRecord(start.toEpochMilli(), methodName, className, packageName);
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Method executed with exception while executing: " + className + "#" +
                    methodName + "." + packageName);
            throw e;
        } finally {
            Instant end = Instant.now();
            timeRecordService.updateTimeRecord(recordId, end.toEpochMilli());
            log.info("Saved execution time to database: {}.{}#{}", packageName, className, methodName);
        }
    }

    /**
     * This advice tracks time of an annotated method in a separate thread, using projectreactor and Mono
     * @param joinPoint join point got from the pointcuts
     * @return returns advised method's return value
     * @throws Throwable throws unchecked exception if advised method did throw an exception
     * The time is tracked even if method finished execution exceptionally
     */
    @Around("trackTimeAsyncAnnotation() && executionPointcut()")
    private Object trackTimeAsync(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();
        final var methodName = getMethodNameFromJoinPoint(joinPoint);
        final var className = getClassNameFromJoinPoint(joinPoint);
        final var packageName = getPackageNameFromJoinPoint(joinPoint);
        // Execute in current thread and return value
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("Method executed with exception while executing: " + className + "#" +
                    methodName + "." + packageName);
            throw e;
        } finally {
            Instant end = Instant.now();
            // Async calculate time of execution
            // First callable and subscribe will be called on a boundedElastic schedulers non-blocking current thread
            Mono.fromCallable(() ->
                    timeRecordService.createNewTimeRecord(start.toEpochMilli(), methodName, className, packageName))
                    .doFinally(signalType -> log.info("Saved execution time to database: {}.{}#{}. Signal: {}",
                            packageName, className, methodName, signalType))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe(id -> timeRecordService.updateTimeRecord(id, end.toEpochMilli()));
        }
    }

    private String getMethodNameFromJoinPoint(JoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint.getSignature())
                .map(Signature::getName).orElseGet(() -> "unknown");
    }

    private String getClassNameFromJoinPoint(JoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint.getThis())
                .map(Object::getClass).map(Class::getSimpleName).orElseGet(() -> "unknown class");
    }

    private String getPackageNameFromJoinPoint(JoinPoint joinPoint) {
        return Optional.ofNullable(joinPoint.getThis())
                .map(Object::getClass).map(Class::getPackage).map(Package::getName)
                .orElseGet(() -> "unknown package");
    }
}
