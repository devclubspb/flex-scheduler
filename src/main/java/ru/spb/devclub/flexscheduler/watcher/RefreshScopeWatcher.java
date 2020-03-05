package ru.spb.devclub.flexscheduler.watcher;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationListener;
import ru.spb.devclub.flexscheduler.TaskRegistry;

import java.util.Map;

@RequiredArgsConstructor
public class RefreshScopeWatcher implements ApplicationListener<RefreshScopeRefreshedEvent> {
    private final Map<String, TaskRegistry> taskRegistryMap;

    @Override
    public void onApplicationEvent(RefreshScopeRefreshedEvent event) {
        taskRegistryMap.values().forEach(TaskRegistry::refreshTriggers);
    }
}
