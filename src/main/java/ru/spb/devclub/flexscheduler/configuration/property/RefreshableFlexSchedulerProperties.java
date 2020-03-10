package ru.spb.devclub.flexscheduler.configuration.property;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;
import static ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry.DEFAULT_POOL_SIZE;
import static ru.spb.devclub.flexscheduler.annotation.FlexScheduledAnnotationBeanPostProcessor.DEFAULT_REGISTRY_NAME;


@RefreshScope
@Configuration
@ConditionalOnClass({RefreshScopeRefreshedEvent.class, org.springframework.cloud.context.scope.refresh.RefreshScope.class})
@ConfigurationProperties(prefix = "flex-scheduler")
public class RefreshableFlexSchedulerProperties {
    @Getter
    @Setter
    private Binding binding;
    @Setter
    private Integer poolSize;
    @Setter
    private Map<String, Object> tasks;
    @Setter
    private Map<String, Registry> registries;

    @Data
    public static class Registry {
        private Binding binding;
        private Integer poolSize;
        private Map<String, Object> tasks;
    }

    public List<RegistryProperty> buildRegistryProperties() {
        if (registries != null) {
            List<RegistryProperty> result = new ArrayList<>();
            for (Entry<String, Registry> entry : registries.entrySet()) {
                RegistryProperty registry = readRegistry(entry.getKey(), entry.getValue());
                result.add(registry);
            }
            return unmodifiableList(result);
        } else {
            Registry registry = new Registry();
            registry.setTasks(tasks);
            return singletonList(readRegistry(DEFAULT_REGISTRY_NAME, registry));
        }
    }

    private RegistryProperty readRegistry(String name, Registry registry) {
        int poolSize = DEFAULT_POOL_SIZE;
        if (registry.getPoolSize() != null) {
            poolSize = registry.getPoolSize();
        } else if (this.poolSize != null) {
            poolSize = this.poolSize;
        }

        List<TaskProperty> tasks = readTasks(null, registry.getTasks());
        return new RegistryProperty(name, poolSize, tasks);
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

                for (Entry<String, Object> entry : valueMap.entrySet()) {
                    String name = StringUtils.hasText(currentName)
                            ? currentName + "." + entry.getKey()
                            : entry.getKey();
                    tasks.addAll(readTasks(name, entry.getValue()));
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


