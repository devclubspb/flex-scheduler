package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import ru.spb.devclub.flexscheduler.TaskSettings;
import ru.spb.devclub.flexscheduler.repository.TaskRegistryRepository;

@RequiredArgsConstructor
public class DataSourceTaskSettingsSupplier implements TaskSettingsSupplier {
    private final TaskRegistryRepository taskRegistryRepository;
    private final String registryName;
    private final String taskName;

    @Override
    public TaskSettings get() {
        return taskRegistryRepository.getTrigger(registryName, taskName);
    }

}
