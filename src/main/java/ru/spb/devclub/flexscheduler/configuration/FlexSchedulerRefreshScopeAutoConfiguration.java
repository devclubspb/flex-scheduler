package ru.spb.devclub.flexscheduler.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.spb.devclub.flexscheduler.TaskRegistry;
import ru.spb.devclub.flexscheduler.watcher.RefreshScopeWatcher;

import java.util.Map;

@Configuration
@ConditionalOnClass({RefreshScopeRefreshedEvent.class, RefreshScope.class})
public class FlexSchedulerRefreshScopeAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnBean(TaskRegistry.class)
    @ConditionalOnProperty("flex-scheduler.refresh-scope.enabled")
    public RefreshScopeWatcher refreshScopeWatcher(Map<String, TaskRegistry> taskRegistryMap) {
        return new RefreshScopeWatcher(taskRegistryMap);
    }
}
