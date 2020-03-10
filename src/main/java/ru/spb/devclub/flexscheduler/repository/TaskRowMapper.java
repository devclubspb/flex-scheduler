package ru.spb.devclub.flexscheduler.repository;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

class TaskRowMapper implements RowMapper<TaskRow> {

    @Override
    public TaskRow mapRow(ResultSet rs, int rowNum) throws SQLException {
        TaskRow row = new TaskRow();
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
    }

    private Long getLongOrNull(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }
}
