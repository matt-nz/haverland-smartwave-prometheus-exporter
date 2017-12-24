package org.trastle.managed;

import io.dropwizard.lifecycle.Managed;
import io.dropwizard.util.Duration;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HaverlandScraperTaskScheduler implements Managed {

    private final Duration interval;
    private Runnable task;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private ScheduledFuture<?> scheduledFuture;

    public HaverlandScraperTaskScheduler(Runnable task, Duration interval) {
        this.interval = interval;
        this.task = task;
    }

    @Override
    public void start() throws Exception {
        scheduledFuture = scheduler.scheduleAtFixedRate(task,
                0, interval.toMilliseconds(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() throws Exception {
        scheduledFuture.cancel(false);
    }
}