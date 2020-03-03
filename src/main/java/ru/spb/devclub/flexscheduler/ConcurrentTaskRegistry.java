package ru.spb.devclub.flexscheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.exception.TaskAlreadyExistsException;
import ru.spb.devclub.flexscheduler.exception.TaskNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

@Slf4j
public class ConcurrentTaskRegistry implements TaskRegistry {
    private static final boolean DEFAULT_MAY_INTERRUPT_IF_RUNNING = false;

    private final Map<String, RegisteredTask> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler executorService = new ThreadPoolTaskScheduler() {{
        setRemoveOnCancelPolicy(true);
    }};
    private final boolean mayInterruptIfRunning;

    public ConcurrentTaskRegistry() {
        this.mayInterruptIfRunning = DEFAULT_MAY_INTERRUPT_IF_RUNNING;
    }

    public ConcurrentTaskRegistry(int poolSize, boolean mayInterruptIfRunning) {
        executorService.setPoolSize(poolSize);
        this.mayInterruptIfRunning = mayInterruptIfRunning;
    }

    @Override
    public void schedule(Task task, boolean overwrite) throws TaskAlreadyExistsException {
        Assert.notNull(task, "task must not be null");
        RegisteredTask registeredTask = new RegisteredTask(task);

        if (overwrite) {
            cancelSilently(registeredTask.getName());
        }

        RegisteredTask previousTask = scheduledTasks.putIfAbsent(task.getName(), registeredTask);
        if (!registeredTask.equals(previousTask)) {
            throw new TaskAlreadyExistsException(registeredTask.getName());
        }

        ScheduledFuture<?> future = executorService.schedule(registeredTask.getCommand(), registeredTask.getTrigger());

        registeredTask.setFuture(future);
        log.info("Registered task: {}", registeredTask.getName());
    }

    private void reSchedule(RegisteredTask registeredTask) {
        cancelSilently(registeredTask.getName());

        scheduledTasks.put(registeredTask.getName(), registeredTask);
        ScheduledFuture<?> future = executorService.schedule(registeredTask.getCommand(), registeredTask.getTrigger());

        registeredTask.setFuture(future);
        log.info("Task {} was re-scheduled", registeredTask.getName());
    }

    @Override
    public void cancel(String taskName) throws TaskNotFoundException {
        RegisteredTask removedTask = scheduledTasks.remove(taskName);
        if (removedTask == null) {
            throw new TaskNotFoundException(taskName);
        }

        removedTask.getFuture().cancel(mayInterruptIfRunning);
        log.info("Cancelled task: {}", taskName);
    }

    @Override
    public void cancelSilently(String taskName) {
        RegisteredTask removedTask = scheduledTasks.remove(taskName);
        if (removedTask != null) {
            removedTask.getFuture().cancel(mayInterruptIfRunning);
            log.info("Cancelled task: {}", taskName);
        }
    }

    @Override
    public List<ObservableTask> getList() {
        return scheduledTasks.values().stream()
                .map(ObservableTask::new)
                .collect(Collectors.toList());
    }

    @Override
    public void refreshTriggers() {
        scheduledTasks.forEach((taskName, registeredTask) -> {
            Trigger lastTrigger = registeredTask.getLastTrigger();
            Trigger newTrigger = registeredTask.getTrigger();

            if (newTrigger.equals(lastTrigger)) {
                log.debug("Trigger did not changed for taskName: {}", taskName);
            } else {
                reSchedule(registeredTask);
            }
        });
    }
}
