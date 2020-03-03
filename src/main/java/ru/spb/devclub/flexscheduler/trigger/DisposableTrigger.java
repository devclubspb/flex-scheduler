package ru.spb.devclub.flexscheduler.trigger;

import lombok.Data;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Data
public class DisposableTrigger implements Trigger {
    private final long scheduledTime;
    private final long after;

    public DisposableTrigger(long after, TimeUnit timeUnit) {
        Assert.isTrue(after >= 0, "after must not be negative");

        this.after = timeUnit.toMillis(after);
        this.scheduledTime = System.currentTimeMillis();
    }

    @Override
    public Date nextExecutionTime(TriggerContext triggerContext) {
        if (triggerContext.lastActualExecutionTime() == null) {
            return new Date(scheduledTime + after);
        } else {
            return null;
        }
    }
}
