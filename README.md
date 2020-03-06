# Flex-scheduler

[![Build Status](https://travis-ci.com/devclubspb/flex-scheduler.svg?branch=master)](https://travis-ci.com/devclubspb/flex-scheduler)

Flex scheduler for Spring-boot applications with on-fly config changes.

## Architecture

```
TaskProducer -(create)-> Task -(schedule)-> TaskRegistry <-(refreshTriggers)- TaskWatcher
```
