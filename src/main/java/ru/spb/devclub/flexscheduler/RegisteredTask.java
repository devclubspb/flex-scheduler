package ru.spb.devclub.flexscheduler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.scheduling.Trigger;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"future", "triggerSupplier", "command"})
class RegisteredTask {
    @Getter
    private final String name;
    private final TriggerSupplier triggerSupplier;
    @Getter
    private final ObservableRunnable command;
    @Getter
    private final boolean mayInterruptIfRunning;
    @Getter
    @Setter
    private volatile ScheduledFuture<?> future;

    @Getter
    private volatile LocalDateTime lastLaunchDate;
    @Getter
    private volatile LocalDateTime lastFinishedDate;
    @Getter
    private volatile boolean isActive;
    private final AtomicInteger launchedCount = new AtomicInteger();
    private final AtomicReference<Trigger> lastTrigger = new AtomicReference<>();

    public RegisteredTask(Task task) {
        Assert.notNull(task, "task must not be null");
        Assert.notNull(task.getName(), "task name must not be null");
        Assert.notNull(task.getCommand(), "task command must not be null");
        Assert.notNull(task.getTriggerSupplier(), "task trigger must not be null");

        this.name = task.getName();
        this.triggerSupplier = task.getTriggerSupplier();
        this.command = new ObservableRunnable(task.getCommand());
        this.mayInterruptIfRunning = task.isMayInterruptIfRunning();
    }

    public Trigger fetchTrigger() {
        Trigger trigger = triggerSupplier.get();
        Assert.notNull(trigger, "triggerSupplier returned null trigger for taskName: " + name);

        lastTrigger.set(trigger);
        return trigger;
    }

    public Trigger getLastTrigger() {
        return lastTrigger.get();
    }

    public int getLaunchedCount() {
        return launchedCount.get();
    }

    @RequiredArgsConstructor
    public class ObservableRunnable implements Runnable {
        private final Runnable runnable;

        @Override
        public void run() {
            launchedCount.incrementAndGet();
            lastLaunchDate = LocalDateTime.now();
            isActive = true;

            runnable.run();

            lastFinishedDate = LocalDateTime.now();
            isActive = false;
        }
    }
}
