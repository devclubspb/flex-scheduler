package ru.spb.devclub.flexscheduler.annotation;

import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import ru.spb.devclub.flexscheduler.Task;
import ru.spb.devclub.flexscheduler.TaskRegistry;
import ru.spb.devclub.flexscheduler.supplier.TriggerSupplier;
import ru.spb.devclub.flexscheduler.trigger.DisposableTrigger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Grig Alex
 * @see org.springframework.scheduling.annotation.ScheduledAnnotationBeanPostProcessor
 */
public class FlexScheduledAnnotationBeanPostProcessor implements BeanPostProcessor {
    private static final MethodIntrospector.MetadataLookup<Set<FlexScheduled>> SELECTOR = method -> {
        Set<FlexScheduled> methods =
                AnnotatedElementUtils.getMergedRepeatableAnnotations(method, FlexScheduled.class);
        return !methods.isEmpty() ? methods : null;
    };

    private final Map<String, TaskRegistry> taskRegistries;
    private final Set<Class<?>> nonAnnotatedClasses;

    public FlexScheduledAnnotationBeanPostProcessor(Map<String, TaskRegistry> taskRegistries) {
        this.taskRegistries = taskRegistries;
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
                    annotations.forEach(annotation -> processFlexScheduled(annotation, method, bean));
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
        final String taskName = createTaskName(annotation, method, bean);
        final TriggerSupplier triggerSupplier = createTriggerSupplier(annotation, method, bean);
        final Runnable runnable = createRunnable(annotation, method, bean);
        return new Task(taskName, runnable, triggerSupplier);
    }

    private String createTaskName(FlexScheduled annotation, Method method, Object bean) {
        return method.getClass().getName() + "#" + method.getName();
    }

    private TriggerSupplier createTriggerSupplier(FlexScheduled annotation, Method method, Object bean) {
        return () -> new DisposableTrigger(annotation.fixedDelay(), TimeUnit.MILLISECONDS);
    }

    private Runnable createRunnable(FlexScheduled annotation, Method method, Object bean) {
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
