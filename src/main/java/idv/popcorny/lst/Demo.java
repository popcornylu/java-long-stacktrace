package idv.popcorny.lst;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Demo {
    private static LongStackTraceExecutor executor;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newCachedThreadPool();
        executor = new LongStackTraceExecutor(executorService);


        CompletableFuture.runAsync(() -> {
            CompletableFuture.runAsync(() -> {
                CompletableFuture.runAsync(() -> {
                    LongStackTraceExecutor.dumpStack();
                }, executor);
            }, executor);
        },executor);

        Thread.sleep(5000);
        executorService.shutdownNow();
    }
}
