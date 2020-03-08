package ru.spb.devclub.flexscheduler.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry;
import ru.spb.devclub.flexscheduler.TaskRegistry;

@Configuration
public class FlexSchedulerAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TaskRegistry taskRegistry() {
        return new ConcurrentTaskRegistry();
    }
}
