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
import static ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry.DEFAULT_MAY_INTERRUPT_IF_RUNNING;
import static ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry.DEFAULT_POOL_SIZE;
import static ru.spb.devclub.flexscheduler.annotation.FlexScheduledAnnotationBeanPostProcessor.DEFAULT_REGISTRY_NAME;

@ExtendWith(SpringExtension.class)
@EnableConfigurationProperties(value = RefreshableFlexSchedulerProperties.class)
@PropertySource(value = "classpath:refresh-scope-default-registry.yml", factory = YamlPropertyLoaderFactory.class)
class RefreshableFlexSchedulerPropertiesDefaultRegistryTest {

    @Autowired
    RefreshableFlexSchedulerProperties properties;


    @Test
    void propertiesParsing() {
        List<RegistryProperty> registries = properties.buildRegistryProperties();
        assertThat(registries).containsExactly(
                new RegistryProperty(DEFAULT_REGISTRY_NAME, DEFAULT_MAY_INTERRUPT_IF_RUNNING, DEFAULT_POOL_SIZE,
                        Arrays.asList(
                                new TaskProperty("events.update", "0 0 * * * *", null, null, null),
                                new TaskProperty("events.queue", "1 1 * * * *", null, null, null),
                                new TaskProperty("festivals.new.update", null, 1000L, null, 2000L),
                                new TaskProperty("festivals.new.queue", null, null, 3000L, null)
                        )
                ));
        assertThat(properties.getBinding()).isEqualTo(Binding.PROPERTY);
    }

}
