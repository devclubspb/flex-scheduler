-- TODO two tables, registry settings
CREATE TABLE scheduler
(
    registry_name VARCHAR(140) NOT NULL,
    task_name     VARCHAR(140) NOT NULL,
    cron          VARCHAR(1000),
    period        BIGINT,
    time_unit     VARCHAR(255),
    initial_delay BIGINT,
    fixed_rate    BOOLEAN,
    check (
        (cron is not null AND period is null AND time_unit is null AND initial_delay is null AND fixed_rate is null) or
        (cron is null AND period is not null AND time_unit is not null AND initial_delay is not null AND fixed_rate is not null)),
    check ( time_unit is null or time_unit in ('NANOSECONDS',
                                               'MICROSECONDS',
                                               'MILLISECONDS',
                                               'SECONDS',
                                               'MINUTES',
                                               'HOURS',
                                               'DAYS') ),
    PRIMARY KEY (registry_name, task_name)
);
