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
import ru.spb.devclub.flexscheduler.ConcurrentTaskRegistry;
import ru.spb.devclub.flexscheduler.Task;
import ru.spb.devclub.flexscheduler.TaskRegistry;
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

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private final TaskRegistry taskRegistry = getTaskRegistry();

    protected TaskRegistry getTaskRegistry() {
        return new ConcurrentTaskRegistry();
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        final Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
        if (AnnotationUtils.isCandidateClass(targetClass, FlexScheduled.class)) {
            final Map<Method, Set<FlexScheduled>> annotatedMethods = getAnnotatedMethods(targetClass);
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(targetClass);
            } else {
                annotatedMethods.forEach((method, flexScheduledMethods) -> {
                    flexScheduledMethods.forEach(flexScheduled -> {
                        processFlexScheduled(flexScheduled, method, bean);
                    });
                });
            }
        }
        return bean;
    }

    private Map<Method, Set<FlexScheduled>> getAnnotatedMethods(Class<?> targetType) {
        return MethodIntrospector.selectMethods(targetType, SELECTOR);
    }

    private void processFlexScheduled(FlexScheduled flexScheduled, Method method, Object bean) {
        Runnable runnable = createRunnable(method, bean);
        final DisposableTrigger trigger = new DisposableTrigger(1, TimeUnit.SECONDS);
        final Task task = new Task("name", trigger, runnable);
        taskRegistry.schedule(task, false);

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
