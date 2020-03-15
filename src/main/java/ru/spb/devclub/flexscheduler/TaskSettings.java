package ru.spb.devclub.flexscheduler;

import lombok.Value;

@Value
public class TaskSettings {
    private final String cron;
    private final Long fixedDelay;
    private final Long fixedRate;
    private final Long initialDelay;
}
