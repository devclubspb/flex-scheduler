package ru.spb.devclub.flexscheduler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.Trigger;

import java.util.concurrent.ScheduledFuture;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {
    private String name;
    private Trigger trigger;
    private Runnable command;
    private ScheduledFuture<?> future;
}
