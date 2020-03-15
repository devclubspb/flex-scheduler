package ru.spb.devclub.flexscheduler.supplier;

import lombok.RequiredArgsConstructor;
import ru.spb.devclub.flexscheduler.TaskSettings;

@RequiredArgsConstructor
public class SimpleTaskSettingsSupplier implements TaskSettingsSupplier {
    private final TaskSettings taskSettings;

    @Override
    public TaskSettings get() {
        return taskSettings;
    }
}
