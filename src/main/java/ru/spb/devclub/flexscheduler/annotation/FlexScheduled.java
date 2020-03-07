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

    String registry() default "concurrentTaskRegistry";

    /**
     * Default: Class#method
     */
    String task() default "";

    Binding binding() default Binding.PROPERTY;
}
