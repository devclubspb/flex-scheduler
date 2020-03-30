package ru.spb.devclub.flexscheduler;

import java.util.List;

public interface TaskRegistry {

    void schedule(Task task, boolean overwrite);

    void cancel(String taskName, boolean silently);

    List<ObservableTask> getList();

    void refreshTriggers();

    void setPoolSize(int value);
}
