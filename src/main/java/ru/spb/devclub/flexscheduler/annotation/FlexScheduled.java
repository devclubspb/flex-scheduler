package ru.spb.devclub.flexscheduler.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Grig Alex
 * @see org.springframework.scheduling.annotation.Scheduled
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FlexScheduled {
    long fixedDelay() default -1;
}
