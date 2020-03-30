package ru.spb.devclub.flexscheduler.annotation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry;

@SpringBootTest(classes = {
        ConcurrentTaskRegistry.class,
        FlexScheduledAnnotationBeanPostProcessor.class,
        FlexScheduledAnnotationBeanPostProcessorTest.Tasks.class
})
public class FlexScheduledAnnotationBeanPostProcessorTest {
    @Autowired
    private ConcurrentTaskRegistry concurrentTaskRegistry;

    @Test
    public void should_registers_tasks_to_concurrentTaskRegistry() {
        Assertions.assertEquals(2, concurrentTaskRegistry.getList().size());
    }

    public static class Tasks {
        @FlexScheduled
        public void task1() {
        }

        @FlexScheduled
        public void task2() {
        }
    }
}