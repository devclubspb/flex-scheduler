package ru.spb.devclub.flexscheduler.configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.spb.devclub.flexscheduler.configuration.property.Binding;
import ru.spb.devclub.flexscheduler.configuration.property.RefreshableFlexSchedulerProperties;
import ru.spb.devclub.flexscheduler.configuration.property.RegistryProperty;
import ru.spb.devclub.flexscheduler.configuration.property.TaskProperty;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = RefreshableFlexSchedulerProperties.class)
@PropertySource(value = "classpath:refresh-scope-two-registries.yml", factory = YamlPropertyLoaderFactory.class)
class RefreshableFlexSchedulerPropertiesTwoRegistriesTest {

    @Autowired
    RefreshableFlexSchedulerProperties properties;

    @Test
    void propertiesParsing() {
        List<RegistryProperty> registries = properties.buildRegistryProperties();
        assertThat(registries).containsExactly(
                new RegistryProperty("first", 2,
                        Arrays.asList(
                                new TaskProperty("events.update", "0 0 * * * *", null, null, null),
                                new TaskProperty("events.queue", "1 1 * * * *", null, null, null),
                                new TaskProperty("festivals.new.update", null, 1000L, null, 2000L),
                                new TaskProperty("festivals.new.queue", null, null, 3000L, null)
                        )),
                new RegistryProperty("second", 4,
                        Arrays.asList(
                                new TaskProperty("events.update", "2 2 * * * *", null, null, null),
                                new TaskProperty("events.queue", "3 3 * * * *", null, null, null),
                                new TaskProperty("festivals.new.update", null, 4000L, null, 5000L),
                                new TaskProperty("festivals.new.queue", null, null, 6000L, null)
                        ))
        );
        assertThat(properties.getBinding()).isEqualTo(Binding.PROPERTY);
    }

}
