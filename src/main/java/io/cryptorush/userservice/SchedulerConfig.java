package io.cryptorush.userservice;


import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class SchedulerConfig {

    @Value("${scheduler.rest.core.pool.size}")
    private int schedulerRestCorePoolSize;
    @Value("${scheduler.rest.max.pool.size}")
    private int schedulerRestMaxPoolSize;
    @Value("${scheduler.rest.keep.alive.time.secs}")
    private int schedulerRestKeepAliveTime;
    @Value("${scheduler.rest.queue.max.size}")
    private int schedulerRestQueueMaxSize;

    @Value("${scheduler.ext.core.pool.size}")
    private int schedulerExternalCorePoolSize;
    @Value("${scheduler.ext.max.pool.size}")
    private int schedulerExternalMaxPoolSize;
    @Value("${scheduler.ext.keep.alive.time.secs}")
    private int schedulerExternalKeepAliveTime;
    @Value("${scheduler.ext.queue.max.size}")
    private int schedulerExternalQueueMaxSize;

    @Value("${scheduler.graphql.core.pool.size}")
    private int schedulerGraphQlCorePoolSize;
    @Value("${scheduler.graphql.max.pool.size}")
    private int schedulerGraphQlMaxPoolSize;
    @Value("${scheduler.graphql.keep.alive.time.secs}")
    private int schedulerGraphQlKeepAliveTime;
    @Value("${scheduler.graphql.queue.max.size}")
    private int schedulerGraphQlQueueMaxSize;

    @Bean
    @Qualifier("rest-scheduler")
    public Scheduler mainScheduler() {

        Preconditions.checkArgument(schedulerRestCorePoolSize > 0, "Missing scheduler.rest.core.pool.size config!");
        Preconditions.checkArgument(schedulerRestMaxPoolSize > 0, "Missing scheduler.rest.max.pool.size config!");
        Preconditions.checkArgument(schedulerRestKeepAliveTime > 0, "Missing scheduler.rest.keep.alive.time.secs config!");
        Preconditions.checkArgument(schedulerRestQueueMaxSize > 0, "Missing scheduler.rest.queue.max.size config!");

        log.info("Creating REST scheduler: core.pool.size={}, max.pool.size={}, keep.alive.time={}, queue.max.size={}",
                schedulerRestCorePoolSize,
                schedulerRestMaxPoolSize,
                schedulerRestKeepAliveTime,
                schedulerRestQueueMaxSize);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(schedulerRestQueueMaxSize);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                schedulerRestCorePoolSize,
                schedulerRestMaxPoolSize,
                schedulerRestKeepAliveTime,
                TimeUnit.SECONDS,
                queue, new CustomizableThreadFactory("rest-scheduler-t-"));
        return Schedulers.fromExecutor(poolExecutor);
    }

    @Bean
    @Qualifier("ext-scheduler")
    public Scheduler externalCallsScheduler() {

        Preconditions.checkArgument(schedulerExternalCorePoolSize > 0, "Missing scheduler.ext.core.pool.size config!");
        Preconditions.checkArgument(schedulerExternalMaxPoolSize > 0, "Missing scheduler.ext.max.pool.size config!");
        Preconditions.checkArgument(schedulerExternalKeepAliveTime > 0, "Missing scheduler.ext.keep.alive.time.secs config!");
        Preconditions.checkArgument(schedulerExternalQueueMaxSize > 0, "Missing scheduler.ext.queue.max.size config!");

        log.info("Creating EXTERNAL CALLS scheduler: core.pool.size={}, max.pool.size={}, keep.alive.time={}, queue" +
                        ".max.size={}",
                schedulerExternalCorePoolSize,
                schedulerExternalMaxPoolSize,
                schedulerExternalKeepAliveTime,
                schedulerExternalQueueMaxSize);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(schedulerExternalQueueMaxSize);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                schedulerExternalCorePoolSize,
                schedulerExternalMaxPoolSize,
                schedulerExternalKeepAliveTime,
                TimeUnit.SECONDS,
                queue, new CustomizableThreadFactory("external-scheduler-t-"));
        return Schedulers.fromExecutor(poolExecutor);
    }

    @Bean
    @Qualifier("graphql-scheduler")
    public Scheduler graphqlCallsScheduler() {

        Preconditions.checkArgument(schedulerGraphQlCorePoolSize > 0, "Missing scheduler.graphql.core.pool.size config!");
        Preconditions.checkArgument(schedulerGraphQlMaxPoolSize > 0, "Missing scheduler.graphql.max.pool.size config!");
        Preconditions.checkArgument(schedulerGraphQlKeepAliveTime > 0, "Missing scheduler.graphql.keep.alive.time.secs config!");
        Preconditions.checkArgument(schedulerGraphQlQueueMaxSize > 0, "Missing scheduler.graphql.queue.max.size config!");

        log.info("Creating GRAPHQL scheduler: core.pool.size={}, max.pool.size={}, keep.alive.time={}, queue" +
                        ".max.size={}",
                schedulerGraphQlCorePoolSize,
                schedulerGraphQlMaxPoolSize,
                schedulerGraphQlKeepAliveTime,
                schedulerGraphQlQueueMaxSize);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(schedulerGraphQlQueueMaxSize);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                schedulerGraphQlCorePoolSize,
                schedulerGraphQlMaxPoolSize,
                schedulerGraphQlKeepAliveTime,
                TimeUnit.SECONDS,
                queue, new CustomizableThreadFactory("graphql-scheduler-t-"));
        return Schedulers.fromExecutor(poolExecutor);
    }
}
