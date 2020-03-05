package ru.spb.devclub.flexscheduler;

import lombok.Data;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;

@Data
public class Task {
    private final String name;
    private final Runnable command;
    private final TriggerSupplier triggerSupplier;

    public Task(String name, TriggerSupplier triggerSupplier, Runnable command) {
        Assert.notNull(name, "task name must not be null");
        Assert.notNull(command, "task command must not be null");
        Assert.notNull(triggerSupplier, "task trigger supplier must not be null");

        this.name = name;
        this.command = command;
        this.triggerSupplier = triggerSupplier;
    }
}
