package ru.spb.devclub.flexscheduler.watcher;

import lombok.RequiredArgsConstructor;
import ru.spb.devclub.flexscheduler.TaskRegistry;
import ru.spb.devclub.flexscheduler.annotation.Binding;
import ru.spb.devclub.flexscheduler.annotation.FlexScheduled;
import ru.spb.devclub.flexscheduler.repository.TaskRegistryRepository;

import java.util.Map;

@RequiredArgsConstructor
public class DataSourceWatcher {
    private final TaskRegistryRepository taskRegistryRepository;
    private final Map<String, TaskRegistry> taskRegistryMap;

    private long lastChecksum = 0;

    @FlexScheduled(task = "dataSourceWatcher", binding = Binding.PROPERTY) //todo user choice
    public synchronized void check() {
        long currentChecksum = taskRegistryRepository.checksum();

        if (lastChecksum != currentChecksum) {
            taskRegistryMap.values().forEach(TaskRegistry::refreshTriggers);
            lastChecksum = currentChecksum;
        }
    }

}
