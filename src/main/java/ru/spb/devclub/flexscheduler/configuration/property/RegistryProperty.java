package ru.spb.devclub.flexscheduler.configuration.property;

import lombok.Value;

import java.util.List;

@Value
public class RegistryProperty {
    private String name;
    private boolean mayInterruptIfRunning;
    private int poolSize;
    private List<TaskProperty> tasks;
}
