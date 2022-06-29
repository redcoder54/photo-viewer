package redcoder.photoviewer.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskExecutor {

    private static final ThreadPoolExecutor EXECUTOR;

    static {
        int processors = Runtime.getRuntime().availableProcessors();
        EXECUTOR = new ThreadPoolExecutor(processors * 4, processors * 8,
                1, TimeUnit.MINUTES,
                new LinkedBlockingDeque<>(1000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    public static void execute(Runnable runnable) {
        EXECUTOR.execute(runnable);
    }
}
