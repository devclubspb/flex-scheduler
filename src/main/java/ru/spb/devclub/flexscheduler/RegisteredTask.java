package ru.spb.devclub.flexscheduler;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@Data
@RequiredArgsConstructor
class RegisteredTask {
    private final String name;
    private final Trigger trigger;
    private final ObservableRunnable command;

    private ScheduledFuture<?> future;

    private LocalDateTime lastLaunchDate;
    private LocalDateTime lastFinishedDate;
    private boolean isActive;
    private int launchedCount;

    public RegisteredTask(Task task) {
        Assert.notNull(task, "task must not be null");
        Assert.notNull(task.getName(), "task name must not be null");
        Assert.notNull(task.getCommand(), "task command must not be null");
        Assert.notNull(task.getTrigger(), "task trigger must not be null");

        this.name = task.getName();
        this.trigger = task.getTrigger();
        this.command = new ObservableRunnable(task.getCommand());
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
