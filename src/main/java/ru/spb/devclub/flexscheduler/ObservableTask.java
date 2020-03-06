package ru.spb.devclub.flexscheduler;

import lombok.Data;
import org.springframework.scheduling.Trigger;

import java.time.LocalDateTime;

@Data
public class ObservableTask {
    private final String name;
    private final Trigger trigger;

    private final LocalDateTime lastLaunchedDate;
    private final LocalDateTime lastFinishedDate;
    private final boolean active;
    private final int launchedCount;

    public ObservableTask(RegisteredTask registeredTask) {
        this.name = registeredTask.getName();
        this.trigger = registeredTask.getLastTrigger();
        this.lastLaunchedDate = registeredTask.getLastLaunchDate();
        this.lastFinishedDate = registeredTask.getLastFinishedDate();
        this.active = registeredTask.isActive();
        this.launchedCount = registeredTask.getLaunchedCount();
    }
}
