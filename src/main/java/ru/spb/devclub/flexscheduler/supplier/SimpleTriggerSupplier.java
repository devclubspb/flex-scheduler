package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.Trigger;

@RequiredArgsConstructor
public class SimpleTriggerSupplier implements TriggerSupplier {
    private final Trigger trigger;

    @Override
    public Trigger get() {
        return trigger;
    }
}
