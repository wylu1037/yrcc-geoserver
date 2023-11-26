package cn.gov.yrcc.utils.executor;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolConfig {

    @Bean(name = "singleThreadPool")
    public Executor singleThreadPool() {
        return new ThreadPoolExecutor(
                1,
                1,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("single-pool-%d").build(),
                new ThreadPoolExecutor.DiscardPolicy());

    }

    @Bean(name = "fileThreadPool")
    public Executor fileThreadPool() {
        return new ThreadPoolExecutor(
                0,
                10,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000),
                new ThreadFactoryBuilder().setNameFormat("file-pool-%d").build(),
                new ThreadPoolExecutor.DiscardPolicy());

    }
}
