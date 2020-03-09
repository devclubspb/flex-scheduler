package ru.spb.devclub.flexscheduler.configuration.property;


import lombok.Value;

@Value
public class TaskProperty {
    private final String name;
    private final String cron;
    private final Long fixedDelay;
    private final Long fixedRate;
    private final Long initialDelay;
}
