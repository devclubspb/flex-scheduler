package ru.spb.devclub.flexscheduler.configuration.property;

import lombok.Value;

import java.util.List;

@Value
public class RegistryProperty {
    private final String name;
    private final int poolSize;
    private final List<TaskProperty> tasks;
}
