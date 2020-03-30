package ru.spb.devclub.flexscheduler.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FlexSchedules {
    FlexScheduled[] value();
}
