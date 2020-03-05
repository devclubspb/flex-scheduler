package ru.spb.devclub.flexscheduler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;

@RequiredArgsConstructor
class RegisteredTask {
    @Getter
    private final String name;
    private final TriggerSupplier triggerSupplier;
    @Getter
    private final ObservableRunnable command;
    @Getter
    @Setter
    private ScheduledFuture<?> future;

    @Getter
    private LocalDateTime lastLaunchDate;
    @Getter
    private LocalDateTime lastFinishedDate;
    @Getter
    private boolean isActive;
    @Getter
    private int launchedCount;
    @Getter
    private Trigger lastTrigger;

    public RegisteredTask(Task task) {
        Assert.notNull(task, "task must not be null");
        Assert.notNull(task.getName(), "task name must not be null");
        Assert.notNull(task.getCommand(), "task command must not be null");
        Assert.notNull(task.getTriggerSupplier(), "task trigger must not be null");

        this.name = task.getName();
        this.triggerSupplier = task.getTriggerSupplier();
        this.command = new ObservableRunnable(task.getCommand());
    }

    public Trigger getTrigger() {
        Trigger trigger = triggerSupplier.get();
        Assert.notNull(trigger, "triggerSupplier returned null trigger for taskName: " + name);

        lastTrigger = trigger;
        return lastTrigger;
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
