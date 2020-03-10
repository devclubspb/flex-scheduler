package ru.spb.devclub.flexscheduler.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

import java.util.List;

@RequiredArgsConstructor
public class CachedJdbcTaskRegistryRepository implements TaskRegistryRepository {
    private final JdbcTemplate jdbcTemplate;
    private final String tableName;

    @Override
    public Trigger getTrigger(String registryName, String taskName) {
        Object[] args = new Object[]{registryName, taskName};
        String sql = "SELECT * FROM " + tableName + " WHERE registry_name = ? AND task_name = ?";

        List<TaskRow> rows = jdbcTemplate.query(sql, args, new TaskRowMapper());

        Assert.isTrue(rows.size() == 1, String.format("Found %d items for registryName: %s, taskName: %s, which is not 1", rows.size(), registryName, taskName));
        return getTrigger(rows.get(0));
    }

    @Override
    public long checksum() {
        List<TaskRow> rows = jdbcTemplate.query("SELECT * FROM " + tableName, new TaskRowMapper());

        long result = 0;
        for (TaskRow row : rows) {
            result += row.hashCode();
        }
        return result;
    }

    private Trigger getTrigger(TaskRow row) {
        if (row.getCron() == null) {
            PeriodicTrigger trigger = new PeriodicTrigger(row.getPeriod(), row.getTimeUnit());
            trigger.setFixedRate(row.getFixedRate());
            trigger.setInitialDelay(row.getInitialDelay());
            return trigger;
        } else {
            return new CronTrigger(row.getCron());
        }
    }

    @Override
    public Integer getPoolSize(String registryName) {
        return null;
    }
}
