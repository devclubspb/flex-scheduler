package ru.spb.devclub.flexscheduler.repository;

import lombok.Data;

import java.util.concurrent.TimeUnit;

@Data
class TaskRow {
    private String registryName;
    private String taskName;
    private String cron;
    private Long period;
    private TimeUnit timeUnit;
    private Long initialDelay;
    private Boolean fixedRate;
}
