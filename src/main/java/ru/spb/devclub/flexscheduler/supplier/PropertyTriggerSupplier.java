package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;

@RequiredArgsConstructor
public class PropertyTriggerSupplier implements TriggerSupplier {
    private final String propertyName;

    @Override
    public Trigger get() {
        return null;
    }
}
