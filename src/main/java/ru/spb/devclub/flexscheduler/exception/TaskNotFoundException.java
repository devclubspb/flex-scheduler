package ru.spb.devclub.flexscheduler.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(String taskName) {
        super("Task was not found with name: " + taskName);
    }
}
