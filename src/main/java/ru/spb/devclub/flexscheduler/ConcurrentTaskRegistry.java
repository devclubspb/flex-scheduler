package ru.spb.devclub.flexscheduler;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.exception.TaskAlreadyExistsException;
import ru.spb.devclub.flexscheduler.exception.TaskNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class ConcurrentTaskRegistry implements TaskRegistry {
    private final Map<String, Task> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler executorService;

    public ConcurrentTaskRegistry() {
        this.executorService = new ThreadPoolTaskScheduler();
        this.executorService.setRemoveOnCancelPolicy(true);
    }

    public ConcurrentTaskRegistry(int poolSize) {
        this();
        executorService.setPoolSize(poolSize);
    }

    @Override
    public void schedule(Task task, boolean overwrite) throws TaskAlreadyExistsException {
        Assert.notNull(task, "task must not be null");
        Assert.notNull(task.getName(), "task name must not be null");
        Assert.notNull(task.getCommand(), "task command must not be null");
        Assert.notNull(task.getTrigger(), "task trigger must not be null");

        Task previousTask = scheduledTasks.putIfAbsent(task.getName(), task);
        if (!task.equals(previousTask)) {
            throw new TaskAlreadyExistsException(task.getName());
        }

        ScheduledFuture<?> future = executorService.schedule(task.getCommand(), task.getTrigger());
        task.setFuture(future);
    }

    @Override
    public void cancel(String taskName) throws TaskNotFoundException {
        Task removedTask = scheduledTasks.remove(taskName);
        if (removedTask == null) {
            throw new TaskNotFoundException(taskName);
        }

        removedTask.getFuture().cancel(false);
    }

    @Override
    public List<Task> getList() {
        List<Task> tasks = scheduledTasks.values().stream()
                .map(task -> Task.builder()
                        .name(task.getName())
                        .trigger(task.getTrigger())
                        .build())
                .collect(Collectors.toList());

        return Collections.unmodifiableList(tasks);
    }
}
