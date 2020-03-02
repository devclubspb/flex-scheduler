package ru.spb.devclub.flexscheduler;

import ru.spb.devclub.flexscheduler.exception.TaskAlreadyExistsException;
import ru.spb.devclub.flexscheduler.exception.TaskNotFoundException;

import java.util.List;

public interface TaskRegistry {

    void schedule(Task task, boolean overwrite) throws TaskAlreadyExistsException;

    void cancel(String taskName) throws TaskNotFoundException;

    List<Task> getList();

}
