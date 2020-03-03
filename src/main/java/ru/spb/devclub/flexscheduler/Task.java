package ru.spb.devclub.flexscheduler;

import lombok.Data;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;

@Data
public class Task {
    private final String name;
    private final Trigger trigger;
    private final Runnable command;

    public Task(String name, Trigger trigger, Runnable command) {
        Assert.notNull(name, "task name must not be null");
        Assert.notNull(command, "task command must not be null");
        Assert.notNull(trigger, "task trigger must not be null");

        this.name = name;
        this.trigger = trigger;
        this.command = command;
    }
}
