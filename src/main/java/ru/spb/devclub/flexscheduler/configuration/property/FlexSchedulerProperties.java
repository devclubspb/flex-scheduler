package ru.spb.devclub.flexscheduler.configuration.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import ru.spb.devclub.flexscheduler.annotation.Binding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

@Configuration
@ConfigurationProperties(prefix = "flex-scheduler")
public class FlexSchedulerProperties {
    @Getter
    @Setter
    private Binding binding;
    @Setter
    private Map<String, Object> tasks;

    public List<TaskProperty> buildTaskProperties() {
        List<TaskProperty> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry : tasks.entrySet()) {
            List<TaskProperty> tasks = readTasks(entry.getKey(), entry.getValue());
            result.addAll(tasks);
        }
        return unmodifiableList(result);
    }

    @SuppressWarnings("unchecked")
    private List<TaskProperty> readTasks(String currentName, Object value) {
        if (value instanceof String) {
            return singletonList(new TaskProperty(currentName, value.toString(), null, null, null));
        } else {
            Map<String, Object> valueMap = (Map<String, Object>) value;
            List<TaskProperty> tasks = new ArrayList<>();

            if (isTaskPropertyObject(valueMap)) {
                return singletonList(new TaskProperty(
                        currentName,
                        readCron(valueMap),
                        readFixedDelay(valueMap),
                        readFixedRate(valueMap),
                        readInitialDelay(valueMap)
                ));
            } else {

                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    tasks.addAll(readTasks(currentName + "." + entry.getKey(), entry.getValue()));
                }
                return tasks;
            }
        }
    }

    private boolean isTaskPropertyObject(Map<String, Object> valueMap) {
        return valueMap.containsKey("cron") || valueMap.containsKey("fixed-delay") || valueMap.containsKey("fixedDelay")
                || valueMap.containsKey("fixed-rate") || valueMap.containsKey("fixedRate")
                || valueMap.containsKey("initial-delay") || valueMap.containsKey("initialDelay");
    }

    private String readCron(Map<String, Object> map) {
        Object value = map.get("cron");
        return value == null ? null : value.toString();
    }

    private Long readFixedDelay(Map<String, Object> valueMap) {
        return readLongProperty(valueMap, "fixedDelay", "fixed-delay");
    }

    private Long readFixedRate(Map<String, Object> valueMap) {
        return readLongProperty(valueMap, "fixedRate", "fixed-rate");
    }

    private Long readInitialDelay(Map<String, Object> valueMap) {
        return readLongProperty(valueMap, "initialDelay", "initial-delay");
    }

    private Long readLongProperty(Map<String, Object> valueMap, String... names) {
        Object value = null;
        for (String name : names) {
            if (valueMap.containsKey(name)) {
                value = valueMap.get(name);
                break;
            }
        }

        return value == null ? null : ((Number) value).longValue();
    }

}
