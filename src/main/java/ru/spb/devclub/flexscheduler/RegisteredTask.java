package ru.spb.devclub.flexscheduler;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.supplier.TaskSettingsSupplier;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@EqualsAndHashCode(exclude = {"future", "triggerSupplier", "command"})
class RegisteredTask {
    @Getter
    private final String name;
    private final TaskSettingsSupplier taskSettingsSupplier;
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
    private final AtomicReference<TaskSettings> lastSettings = new AtomicReference<>();

    public RegisteredTask(Task task) {
        Assert.notNull(task, "task must not be null");
        Assert.notNull(task.getName(), "task name must not be null");
        Assert.notNull(task.getCommand(), "task command must not be null");
        Assert.notNull(task.getTaskSettingsSupplier(), "task trigger must not be null");

        this.name = task.getName();
        this.taskSettingsSupplier = task.getTaskSettingsSupplier();
        this.command = new ObservableRunnable(task.getCommand());
        this.mayInterruptIfRunning = task.isMayInterruptIfRunning();
    }

    public TaskSettings fetchSettings() {
        TaskSettings taskSettings = taskSettingsSupplier.get();
        Assert.notNull(taskSettings, "taskSettings returned null settings for taskName: " + name);

        lastSettings.set(taskSettings);
        return taskSettings;
    }

    public TaskSettings getLastSettings() {
        return lastSettings.get();
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
