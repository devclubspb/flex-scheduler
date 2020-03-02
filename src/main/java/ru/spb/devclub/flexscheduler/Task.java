package ru.spb.devclub.flexscheduler;

import lombok.Data;
import org.springframework.scheduling.Trigger;

@Data
public class Task {
    private String name;
    private Trigger trigger;
    private Runnable command;
}
