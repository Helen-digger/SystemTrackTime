package com.github.helendigger.tracktime.aspect.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Track method execution time async without blocking working thread
 * Used by {@link com.github.helendigger.tracktime.aspect.TrackTimeAspect TrackTimeAspect}, implementation
 * details also locates there.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface TrackTimeAsync {
}
