package ru.spb.devclub.flexscheduler.configuration;

import io.micrometer.core.instrument.Metrics;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spb.devclub.flexscheduler.TaskRegistry;
import ru.spb.devclub.flexscheduler.metric.FlexSchedulerEndpoint;
import ru.spb.devclub.flexscheduler.metric.FlexSchedulerMetrics;

import java.util.Map;

@Configuration
@ConditionalOnClass({Metrics.class, Endpoint.class})
public class ActuatorFlexSchedulerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(TaskRegistry.class)
    public FlexSchedulerMetrics flexSchedulerMetrics(Map<String, TaskRegistry> taskRegistryMap) {
        return new FlexSchedulerMetrics(taskRegistryMap);
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(TaskRegistry.class)
    public FlexSchedulerEndpoint flexSchedulerEndpoint(Map<String, TaskRegistry> taskRegistryMap) {
        return new FlexSchedulerEndpoint(taskRegistryMap);
    }
}
