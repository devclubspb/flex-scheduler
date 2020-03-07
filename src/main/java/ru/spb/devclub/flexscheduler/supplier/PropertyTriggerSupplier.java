package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;

@RequiredArgsConstructor
public class PropertyTriggerSupplier implements TriggerSupplier {
    private final String registryName;
    private final String taskName;

    @Override
    public Trigger get() {
        String cron = System.getProperty("flex-scheduling." + registryName + "." + taskName + ".cron");
        if (cron != null) {
            return new CronTrigger(cron);
        }
        //todo add spring property binder
        //todo fixedDelay, fixedRate, empty cron
        return null;
    }
}
