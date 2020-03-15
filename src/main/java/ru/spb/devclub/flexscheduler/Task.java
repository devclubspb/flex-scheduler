package ru.spb.devclub.flexscheduler;

import lombok.Value;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TaskSettingsSupplier;

@Value
public class Task {
    private final String name;
    private final Runnable command;
    private final TaskSettingsSupplier taskSettingsSupplier;
    private final boolean mayInterruptIfRunning;

    public Task(String name, Runnable command, TaskSettingsSupplier taskSettingsSupplier, boolean mayInterruptIfRunning) {
        Assert.notNull(name, "task name must not be null");
        Assert.notNull(command, "task command must not be null");
        Assert.notNull(taskSettingsSupplier, "task trigger supplier must not be null");

        this.name = name;
        this.command = command;
        this.taskSettingsSupplier = taskSettingsSupplier;
        this.mayInterruptIfRunning = mayInterruptIfRunning;
    }
}
