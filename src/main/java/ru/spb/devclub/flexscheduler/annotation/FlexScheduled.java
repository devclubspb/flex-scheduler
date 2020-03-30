package ru.spb.devclub.flexscheduler.annotation;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.scheduling.annotation.Schedules;
import ru.spb.devclub.flexscheduler.configuration.property.Binding;

import java.lang.annotation.*;

import static ru.spb.devclub.flexscheduler.annotation.FlexScheduledAnnotationBeanPostProcessor.DEFAULT_REGISTRY_NAME;

/**
 * @author Grig Alex
 * @see org.springframework.scheduling.annotation.Scheduled
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(FlexSchedules.class)
@RefreshScope
public @interface FlexScheduled {

    String registry() default DEFAULT_REGISTRY_NAME;

    /**
     * Default: Class#method
     */
    String task() default "";

    Binding binding() default Binding.PROPERTY;

    boolean mayInterruptIfRunning() default false;
}
