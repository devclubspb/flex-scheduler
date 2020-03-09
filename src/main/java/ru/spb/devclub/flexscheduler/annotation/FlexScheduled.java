package ru.spb.devclub.flexscheduler.annotation;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import ru.spb.devclub.flexscheduler.configuration.property.Binding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static ru.spb.devclub.flexscheduler.annotation.FlexScheduledAnnotationBeanPostProcessor.DEFAULT_REGISTRY_NAME;

/**
 * @author Grig Alex
 * @see org.springframework.scheduling.annotation.Scheduled
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@RefreshScope
public @interface FlexScheduled {

    String registry() default DEFAULT_REGISTRY_NAME;

    /**
     * Default: Class#method
     */
    String task() default "";

    Binding binding() default Binding.PROPERTY;
}
