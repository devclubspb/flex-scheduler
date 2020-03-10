package ru.spb.devclub.flexscheduler.repository;

import org.springframework.scheduling.Trigger;

public interface TaskRegistryRepository {
    Trigger getTrigger(String registryName, String taskName);

    long checksum();

    Integer getPoolSize(String registryName);
}
