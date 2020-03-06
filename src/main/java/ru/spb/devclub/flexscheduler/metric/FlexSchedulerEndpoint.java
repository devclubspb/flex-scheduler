package ru.spb.devclub.flexscheduler.metric;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import ru.spb.devclub.flexscheduler.TaskRegistry;

import java.util.LinkedHashMap;
import java.util.Map;

@Endpoint(id = "flex-scheduler")
@RequiredArgsConstructor
public class FlexSchedulerEndpoint {
    private final Map<String, TaskRegistry> taskRegistryMap;

    @ReadOperation
    public Map<String, Object> summary() {
        Map<String, Object> result = new LinkedHashMap<>();

        taskRegistryMap.forEach((beanName, taskRegistry) -> {
            result.put(beanName, taskRegistry.getList());
        });

        return result;
    }
}
