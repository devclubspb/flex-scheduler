package ru.spb.devclub.flexscheduler.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.spb.devclub.flexscheduler.annotation.Binding;
import ru.spb.devclub.flexscheduler.configuration.property.FlexSchedulerProperties;
import ru.spb.devclub.flexscheduler.configuration.property.FlexSchedulerProperties.TaskProperty;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = FlexSchedulerProperties.class)
@PropertySource(value = "classpath:refresh-scope-default-registry.yml", factory = YamlPropertyLoaderFactory.class)
class FlexSchedulerPropertiesTest {

    @Autowired
    FlexSchedulerProperties properties;


    @Test
    void propertiesParsing() {
        List<TaskProperty> tasks = properties.buildTaskProperties();
        assertThat(tasks).containsExactly(
                new TaskProperty("events.update", "0 0 * * * *", null, null, null),
                new TaskProperty("events.queue", "1 1 * * * *", null, null, null),
                new TaskProperty("festivals.new.update", null, 1000L, null, 2000L),
                new TaskProperty("festivals.new.queue", null, null, 3000L, null)
        );
        assertThat(properties.getBinding()).isEqualTo(Binding.PROPERTY);
    }

}
