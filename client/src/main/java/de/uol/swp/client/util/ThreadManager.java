package de.uol.swp.client.util;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Utility class to handle execution of instructions on a non-JavaFX Application Thread
 * <p>
 * This class uses an ExecutorService to handle the Threads.
 *
 * @author Temmo Junkhoff
 * @author Phillip-André Suhr
 * @see java.util.concurrent.ExecutorService
 * @since 2021-05-24
 */
public class ThreadManager {

    private static final ExecutorService executorService = Executors
            .newFixedThreadPool(10, new ThreadFactoryBuilder().setNameFormat("RunNow-Thread-%d").build());
    private static final ExecutorService threadForInOrderExecution = Executors
            .newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("inOrderExecutionThread").build());

    /**
     * Forces the shutdown of all ExecutorService Threads
     */
    public static void forceShutdown() {
        executorService.shutdownNow();
        threadForInOrderExecution.shutdownNow();
    }

    /**
     * Executes the provided Runnable sequentially
     *
     * @param runnable The Runnable to execute
     */
    public static void runInOrder(Runnable runnable) {
        threadForInOrderExecution.execute(runnable);
    }

    /**
     * Runs the provided Runnable on the ExecutorService's Thread pool
     *
     * @param runnable The Runnable to execute
     */
    public static void runNow(Runnable runnable) {
        executorService.execute(runnable);
    }

    /**
     * Shuts down the ExecutorService Threads orderly
     */
    public static void shutdown() {
        executorService.shutdown();
        threadForInOrderExecution.shutdown();
    }
}
