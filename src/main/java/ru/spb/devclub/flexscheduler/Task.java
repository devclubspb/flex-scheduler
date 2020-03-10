package ru.spb.devclub.flexscheduler;

import lombok.Value;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;

@Value
public class Task {
    private final String name;
    private final Runnable command;
    private final TriggerSupplier triggerSupplier;
    private final boolean mayInterruptIfRunning;

    public Task(String name, Runnable command, TriggerSupplier triggerSupplier, boolean mayInterruptIfRunning) {
        Assert.notNull(name, "task name must not be null");
        Assert.notNull(command, "task command must not be null");
        Assert.notNull(triggerSupplier, "task trigger supplier must not be null");

        this.name = name;
        this.command = command;
        this.triggerSupplier = triggerSupplier;
        this.mayInterruptIfRunning = mayInterruptIfRunning;
    }
}
