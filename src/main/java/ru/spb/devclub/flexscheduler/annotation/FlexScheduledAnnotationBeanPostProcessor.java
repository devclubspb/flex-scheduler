package ru.spb.devclub.flexscheduler.annotation;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import ru.spb.devclub.flexscheduler.Task;
import ru.spb.devclub.flexscheduler.TaskRegistry;
import ru.spb.devclub.flexscheduler.configuration.property.Binding;
import ru.spb.devclub.flexscheduler.repository.TaskRegistryRepository;
import ru.spb.devclub.flexscheduler.supplier.DataSourceTriggerSupplier;
import ru.spb.devclub.flexscheduler.supplier.PropertyTriggerSupplier;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Grig Alex
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 */
public class FlexScheduledAnnotationBeanPostProcessor implements BeanPostProcessor {
    public static final String DEFAULT_REGISTRY_NAME = "concurrentTaskRegistry";

    private static final MethodIntrospector.MetadataLookup<Set<FlexScheduled>> SELECTOR = method -> {
        Set<FlexScheduled> methods =
                AnnotatedElementUtils.getMergedRepeatableAnnotations(method, FlexScheduled.class);
        return !methods.isEmpty() ? methods : null;
    };

    private final Map<String, TaskRegistry> taskRegistries;
    private final Set<Class<?>> nonAnnotatedClasses;
    private final TaskRegistryRepository taskRegistryRepository;

    public FlexScheduledAnnotationBeanPostProcessor(Map<String, TaskRegistry> taskRegistries, @Nullable TaskRegistryRepository taskRegistryRepository) {
        this.taskRegistries = taskRegistries;
        this.taskRegistryRepository = taskRegistryRepository;
        this.nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (AnnotationUtils.isCandidateClass(targetClass, FlexScheduled.class)) {
            final Map<Method, Set<FlexScheduled>> methodAnnotations = getMethodsWithFlexScheduledAnnotation(targetClass);
            if (methodAnnotations.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
            } else {
                for (Map.Entry<Method, Set<FlexScheduled>> entry : methodAnnotations.entrySet()) {
                    final Method method = entry.getKey();
                    final Set<FlexScheduled> annotations = entry.getValue();
                    for (FlexScheduled annotation : annotations) {
                        processFlexScheduled(annotation, method, bean);
                    }
                }
            }
        }
        return bean;
    }

    private Map<Method, Set<FlexScheduled>> getMethodsWithFlexScheduledAnnotation(Class<?> targetType) {
        return MethodIntrospector.selectMethods(targetType, SELECTOR);
    }

    private void processFlexScheduled(FlexScheduled annotation, Method method, Object bean) {
        final Task task = createTask(annotation, method, bean);
        final String registryName = annotation.registry();
        final TaskRegistry taskRegistry = taskRegistries.get(registryName);
        try {
            taskRegistry.schedule(task, false);
        } catch (Exception e) {
            e.printStackTrace(); //TODO: Handle exceptions
        }
    }

    private Task createTask(FlexScheduled annotation, Method method, Object bean) {
        final String taskName = createTaskName(annotation, method);
        final Runnable runnable = createRunnable(method, bean);
        final TriggerSupplier triggerSupplier = createTriggerSupplier(annotation, taskName);
        return new Task(taskName, runnable, triggerSupplier, annotation.mayInterruptIfRunning());
    }

    private String createTaskName(FlexScheduled annotation, Method method) {
        final String taskName = annotation.task();
        return StringUtils.hasText(taskName) ? taskName : createDefaultTaskName(method);
    }

    private String createDefaultTaskName(Method method) {
        return method.getClass().getName() + "#" + method.getName();
    }

    private TriggerSupplier createTriggerSupplier(FlexScheduled annotation, String taskName) {
        if (annotation.binding() == Binding.PROPERTY) {
            return new PropertyTriggerSupplier(annotation.registry(), taskName);
        } else {
            Assert.notNull(taskRegistryRepository, taskName + " method uses @FlexScheduled with DataSource binding, but there is no TaskRegistryRepository");
            return new DataSourceTriggerSupplier(taskRegistryRepository, annotation.registry(), taskName);
        }
    }

    private Runnable createRunnable(Method method, Object bean) {
        Assert.isTrue(method.getParameterCount() == 0, "Only no-arg methods may be annotated with @FlexScheduled");
        Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
        return () -> {
            ReflectionUtils.makeAccessible(invocableMethod);
            try {
                method.invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace(); //TODO: Handle exceptions
            }
        };
    }
}
