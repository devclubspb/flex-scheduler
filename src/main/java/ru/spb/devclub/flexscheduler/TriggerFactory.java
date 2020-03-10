package ru.spb.devclub.flexscheduler;

import org.springframework.lang.Nullable;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

public class TriggerFactory {

    public Trigger createTrigger(@Nullable String cron, @Nullable Long period, @Nullable TimeUnit timeUnit) {
        if (cron == null) {
            Assert.notNull(period, "Cron or period must not be null");
            PeriodicTrigger trigger = new PeriodicTrigger(period, timeUnit);
            trigger.setFixedRate();
            trigger.setInitialDelay();
            return trigger;
        } else {
            return new CronTrigger(cron);
        }
    }
}
