package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import ru.spb.devclub.flexscheduler.TaskSettings;
import ru.spb.devclub.flexscheduler.configuration.property.RefreshableFlexSchedulerProperties;

@RequiredArgsConstructor
public class PropertyTaskSettingsSupplier implements TaskSettingsSupplier {
    private final RefreshableFlexSchedulerProperties refreshableFlexSchedulerProperties;
    private final String registryName;
    private final String taskName;

    @Override
    public TaskSettings get() {
        String cron = System.getProperty("flex-scheduling." + registryName + "." + taskName + ".cron");


        //todo add spring property binder
        //todo fixedDelay, fixedRate, empty cron
        return null;
    }
}
