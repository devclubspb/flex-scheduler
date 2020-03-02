package ru.spb.devclub.flexscheduler;

import lombok.Data;

@Data
public class Task {
    private String name;
    private Trigger trigger;
    private Runnable command;
}
