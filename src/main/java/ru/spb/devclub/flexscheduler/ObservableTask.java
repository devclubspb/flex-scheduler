package ru.spb.devclub.flexscheduler;

import org.springframework.scheduling.Trigger;

import java.time.LocalDateTime;

public interface ObservableTask {
    String getName();
    Trigger getTrigger();
    LocalDateTime getLastLaunchDate();
    LocalDateTime getLastFinishedDate();
    boolean isActive();
    int getLaunchedCount();
}
