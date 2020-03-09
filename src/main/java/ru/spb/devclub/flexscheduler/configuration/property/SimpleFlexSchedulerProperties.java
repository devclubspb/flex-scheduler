package ru.spb.devclub.flexscheduler.configuration.property;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@ConditionalOnMissingClass("org.springframework.cloud.context.scope.refresh.RefreshScope")
@ConfigurationProperties(prefix = "flex-scheduler")
public class SimpleFlexSchedulerProperties {
    private Binding binding;
    private String tableName;
}


