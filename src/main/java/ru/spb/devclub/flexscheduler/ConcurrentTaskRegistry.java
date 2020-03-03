package ru.spb.devclub.flexscheduler;

import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;
import ru.spb.devclub.flexscheduler.exception.TaskAlreadyExistsException;
import ru.spb.devclub.flexscheduler.exception.TaskNotFoundException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Collectors;

public class ConcurrentTaskRegistry implements TaskRegistry {
    private static final boolean DEFAULT_MAY_INTERRUPT_IF_RUNNING = false;

    private final Map<String, RegisteredTask> scheduledTasks = new ConcurrentHashMap<>();
    private final ThreadPoolTaskScheduler executorService = new ThreadPoolTaskScheduler();
    private final boolean mayInterruptIfRunning;

    {
        this.executorService.setRemoveOnCancelPolicy(true);
    }

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

        RegisteredTask previousTask = scheduledTasks.putIfAbsent(task.getName(), registeredTask);
        if (!registeredTask.equals(previousTask)) {
            throw new TaskAlreadyExistsException(registeredTask.getName());
        }

        ScheduledFuture<?> future = executorService.schedule(registeredTask.getCommand(), registeredTask.getTrigger());
        registeredTask.setFuture(future);
    }

    @Override
    public void cancel(String taskName) throws TaskNotFoundException {
        RegisteredTask removedTask = scheduledTasks.remove(taskName);
        if (removedTask == null) {
            throw new TaskNotFoundException(taskName);
        }

        removedTask.getFuture().cancel(mayInterruptIfRunning);
    }

    @Override
    public List<ObservableTask> getList() {
        return scheduledTasks.values().stream()
                .map(ObservableTask::new)
                .collect(Collectors.toList());
    }
}
