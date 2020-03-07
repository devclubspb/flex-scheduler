package ru.spb.devclub.flexscheduler.repository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.util.Assert;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class JdbcTaskRegistryRepository implements TaskRegistryRepository {
    private static final RowMapper<Row> ROW_MAPPER = (rs, rowNum) -> {
        Row row = new Row();
        row.setRegistryName(rs.getString("registry_name"));
        row.setTaskName(rs.getString(("task_name")));
        row.setCron(rs.getString("cron"));
        row.setPeriod(getLongOrNull(rs, "period"));
        String timeUnitStr = rs.getString("time_unit");
        if (!rs.wasNull()) {
            row.setTimeUnit(TimeUnit.valueOf(timeUnitStr));
        }
        row.setInitialDelay(getLongOrNull(rs, "initial_delay"));
        boolean fixedRate = rs.getBoolean("fixed_rate");
        if (!rs.wasNull()) {
            row.setFixedRate(fixedRate);
        }
        return row;
    };

    private static Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    private final JdbcTemplate jdbcTemplate;
    private final String tableName;

    @Override
    public Trigger getTrigger(String registryName, String taskName) {
        Object[] args = new Object[]{registryName, taskName};
        String sql = "SELECT * FROM " + tableName + " WHERE registry_name = ? AND task_name = ?";

        List<Row> rows = jdbcTemplate.query(sql, args, ROW_MAPPER);

        Assert.isTrue(rows.size() == 1, String.format("Found %d items for registryName: %s, taskName: %s, which is not 1", rows.size(), registryName, taskName));
        return getTrigger(rows.get(0));
    }

    @Override
    public long checksum() {
        List<Row> rows = jdbcTemplate.query("SELECT * FROM " + tableName, ROW_MAPPER);

        long result = 0;
        for (Row row : rows) {
            result += row.hashCode();
        }
        return result;
    }

    private Trigger getTrigger(Row row) {
        if (row.getCron() == null) {
            PeriodicTrigger trigger = new PeriodicTrigger(row.getPeriod(), row.getTimeUnit());
            trigger.setFixedRate(row.getFixedRate());
            trigger.setInitialDelay(row.getInitialDelay());
            return trigger;
        } else {
            return new CronTrigger(row.getCron());
        }
    }

    @Data
    private static class Row {
        private String registryName;
        private String taskName;
        private String cron;
        private Long period;
        private TimeUnit timeUnit;
        private Long initialDelay;
        private Boolean fixedRate;
    }
}
