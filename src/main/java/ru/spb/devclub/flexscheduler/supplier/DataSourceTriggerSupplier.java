package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import ru.spb.devclub.flexscheduler.repository.TaskRegistryRepository;

@RequiredArgsConstructor
public class DataSourceTriggerSupplier implements TriggerSupplier {
    private final TaskRegistryRepository taskRegistryRepository;
    private final String registryName;
    private final String taskName;

    @Override
    public Trigger get() {
        return taskRegistryRepository.getTrigger(registryName, taskName);
    }

}
