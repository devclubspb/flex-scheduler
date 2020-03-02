package ru.spb.devclub.flexscheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@Data
public class Task implements ObservableTask {
    private String name;
    private Trigger trigger;
    private ObservableRunnable command;
    private ScheduledFuture<?> future;

    private LocalDateTime lastLaunchDate;
    private LocalDateTime lastFinishedDate;
    private boolean isActive;
    private int launchedCount;

    public Task() {
    }

    public Task(String name, Trigger trigger, Runnable command) {
        this.name = name;
        this.trigger = trigger;
        this.command = new ObservableRunnable(command);
    }

    @RequiredArgsConstructor
    private class ObservableRunnable implements Runnable {
        private final Runnable runnable;

        @Override
        public void run() {
            launchedCount++;
            lastFinishedDate = LocalDateTime.now();
            isActive = true;

            runnable.run();

            lastFinishedDate = LocalDateTime.now();
            isActive = false;
        }
    }
}
