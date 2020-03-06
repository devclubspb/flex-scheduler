package ru.spb.devclub.flexscheduler.metric;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.binder.MeterBinder;
import lombok.RequiredArgsConstructor;
import ru.spb.devclub.flexscheduler.TaskRegistry;

import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class FlexSchedulerMetrics implements MeterBinder {
    private final Map<String, TaskRegistry> taskRegistryMap;

    @Override
    public void bindTo(MeterRegistry registry) {
        taskRegistryMap.forEach((beanName, taskRegistry) -> {
            Tag registryNameTag = Tag.of("registry_name", beanName);

            taskRegistry.getList().forEach(observableTask -> {
                Tag taskNameTag = Tag.of("task_name", observableTask.getName());
                List<Tag> tags = Arrays.asList(registryNameTag, taskNameTag);

                registry.gauge("launched_count", tags, observableTask.getLaunchedCount());
                registry.gauge("last_launched_date", tags, observableTask.getLastLaunchedDate().toEpochSecond(ZoneOffset.UTC));
                registry.gauge("last_finished_date", tags, observableTask.getLastFinishedDate().toEpochSecond(ZoneOffset.UTC));
                registry.gauge("is_active", tags, observableTask.isActive() ? 1 : 0);
            });
        });
    }
}
