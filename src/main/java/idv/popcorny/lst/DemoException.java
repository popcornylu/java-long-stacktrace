package idv.popcorny.lst;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by popcorny on 8/19/15.
 */
public class DemoException {
    private static LongStackTraceExecutor executor;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executor = new LongStackTraceExecutor(executorService);

        CompletableFuture.runAsync(() -> {
            CompletableFuture.runAsync(() -> {
                CompletableFuture
                .runAsync(LongStackTraceExecutor.tryThrow(() -> {
                    throw new NullPointerException("Just Testing");
                }), executor)
                .whenComplete((Void result, Throwable e) -> {
                    e.printStackTrace();
                });
            }, executor);
        }, executor);

        Thread.sleep(5000);
        executorService.shutdownNow();
    }
}
