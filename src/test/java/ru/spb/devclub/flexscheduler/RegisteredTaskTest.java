package ru.spb.devclub.flexscheduler;

import org.jetbrains.kotlinx.lincheck.LinChecker;
import org.jetbrains.kotlinx.lincheck.LoggingLevel;
import org.jetbrains.kotlinx.lincheck.annotations.OpGroupConfig;
import org.jetbrains.kotlinx.lincheck.annotations.Operation;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressCTest;
import org.jetbrains.kotlinx.lincheck.strategy.stress.StressOptions;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.LocalDateTime;
import java.util.Objects;

@OpGroupConfig(name = "producer", nonParallel = true)
@StressCTest
public class RegisteredTaskTest {
    public RegisteredTask registeredTask = new RegisteredTask(new Task("name", () -> {}, () -> new PeriodicTrigger(1)));

//    @Operation
//    public Integer getLaunchedCount() {
//        return registeredTask.getLaunchedCount();
//    }

    @Operation
    public String getLastFinishedDate() {
        LocalDateTime date = registeredTask.getLastFinishedDate();
        return date == null ? null : date.toString();
    }

//    @Operation
//    public LocalDateTime getLastLaunchDate() {
//        return registeredTask.getLastLaunchDate();
//    }
//
//    @Operation
//    public Trigger getLastTrigger() {
//        return registeredTask.getLastTrigger();
//    }
//
//    @Operation
//    public void fetchTrigger() {
//        registeredTask.fetchTrigger();
//    }
//
//    @Operation
//    public Trigger getLastTrigger() {
//        return registeredTask.getLastTrigger();
//    }

    @Operation(group = "producer")
    public void runCommand() {
        registeredTask.getCommand().run();
    }

    @Test
    public void name() {
        StressOptions opts = new StressOptions()
                .iterations(10)
                .threads(2)
                .logLevel(LoggingLevel.INFO);
        LinChecker.check(RegisteredTaskTest.class, opts);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisteredTaskTest that = (RegisteredTaskTest) o;
        return Objects.equals(registeredTask, that.registeredTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registeredTask);
    }
}
