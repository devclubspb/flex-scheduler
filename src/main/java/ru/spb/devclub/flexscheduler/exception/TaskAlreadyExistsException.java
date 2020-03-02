package ru.spb.devclub.flexscheduler.exception;

public class TaskAlreadyExistsException extends RuntimeException {
    public TaskAlreadyExistsException(String taskName) {
        super("Already registered task with name: " + taskName);
    }
}
