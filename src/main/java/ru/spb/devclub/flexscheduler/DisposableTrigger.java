package ru.spb.devclub.flexscheduler;

import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DisposableTrigger implements Trigger {

    public DisposableTrigger(long after, TimeUnit timeUnit) {
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        return null;
    }
}
