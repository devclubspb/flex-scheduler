package ru.spb.devclub.flexscheduler.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.FilteredClassLoader;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import ru.spb.devclub.flexscheduler.watcher.RefreshScopeWatcher;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

class RefreshScopeWatcherConfigurationTest {

    @Test
    public void whenDependentClassIsNotPresent_thenBeanMissing() {
        new ApplicationContextRunner().withUserConfiguration(RefreshScopeWatcherAutoConfiguration.class)
                .withClassLoader(new FilteredClassLoader(RefreshScopeRefreshedEvent.class))
                .run(context -> assertThat(context).doesNotHaveBean(RefreshScopeWatcher.class));
    }
}
