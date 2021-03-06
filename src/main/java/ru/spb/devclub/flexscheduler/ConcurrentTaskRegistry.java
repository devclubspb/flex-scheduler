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
    public static final boolean DEFAULT_MAY_INTERRUPT_IF_RUNNING = false;
    public static final int DEFAULT_POOL_SIZE = 1;

    private final Map<String, RegisteredTask> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler executorService = new ThreadPoolTaskScheduler() {{
        setRemoveOnCancelPolicy(true);
    }};

    @Override
    public void schedule(Task task, boolean overwrite) {
        Assert.notNull(task, "task must not be null");
        RegisteredTask registeredTask = new RegisteredTask(task);

        schedule(registeredTask, overwrite);
    }

    private void schedule(RegisteredTask registeredTask, boolean overwrite) {
        if (overwrite) {
            cancel(registeredTask.getName(), true);
        }

        RegisteredTask previousTask = scheduledTasks.putIfAbsent(registeredTask.getName(), registeredTask);
        if (!registeredTask.equals(previousTask)) {
            throw new TaskAlreadyExistsException(registeredTask.getName());
        }

        ScheduledFuture<?> future = executorService.schedule(registeredTask.getCommand(), registeredTask.fetchTrigger());

        registeredTask.setFuture(future);
        log.info("Registered task: {}", registeredTask.getName());
    }

    @Override
    public void cancel(String taskName, boolean silently) {
        RegisteredTask removedTask = scheduledTasks.remove(taskName);
        if (removedTask == null) {
            if (!silently) {
                throw new TaskNotFoundException(taskName);
            } else {
                log.debug("Cannot cancel task. Task was not found with name: {}", taskName);
            }
        } else {
            removedTask.getFuture().cancel(removedTask.isMayInterruptIfRunning());
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
            //todo fix double fetch: here and in schedule()
            Trigger newTrigger = registeredTask.fetchTrigger();

            if (newTrigger.equals(lastTrigger)) {
                log.debug("Trigger did not changed for taskName: {}", taskName);
            } else {
                schedule(registeredTask, true);
            }
        });
    }

    @Override
    public void setPoolSize(int value) {
        this.executorService.setPoolSize(value);
    }
}
