package ru.spb.devclub.flexscheduler.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.springframework.dao.support.DataAccessUtils.singleResult;

@RequiredArgsConstructor
public class JdbcTaskRegistryRepository implements TaskRegistryRepository {
    private static final RowMapper<Trigger> TRIGGER_ROW_MAPPER = (rs, rowNum) -> {
        String cron = rs.getString("cron");

        if (rs.wasNull()) {
            TimeUnit timeUnit = TimeUnit.valueOf(rs.getString("time_unit"));

            PeriodicTrigger trigger = new PeriodicTrigger(rs.getLong("period"), timeUnit);
            trigger.setFixedRate(rs.getBoolean("fixed_rate"));
            trigger.setInitialDelay(rs.getLong("initial_delay"));
            return trigger;
        } else {
            return new CronTrigger(cron);
        }
    };

    private final JdbcTemplate jdbcTemplate;
    private final String tableName;

    @Override
    public Trigger getTrigger(String registryName, String taskName) {
        Object[] args = new Object[]{registryName, taskName};
        String sql = "SELECT * FROM " + tableName + " WHERE registry_name = ? AND task_name = ?";

        List<Trigger> result = jdbcTemplate.query(sql, args, TRIGGER_ROW_MAPPER);
        return singleResult(result);
    }
}
