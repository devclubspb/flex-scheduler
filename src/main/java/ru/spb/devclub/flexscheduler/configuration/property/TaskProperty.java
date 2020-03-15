package ru.spb.devclub.flexscheduler.configuration.property;


import lombok.Value;
import ru.spb.devclub.flexscheduler.TaskSettings;

@Value
public class TaskProperty {
    private final String name;
    private final TaskSettings taskSettings;
}
