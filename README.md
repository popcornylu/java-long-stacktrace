# java-long-stacktrace
Long stacktrace is to trace the call stack from both synchronous and asynchronous function invocations. This project is a proof-of-concept application to implement a long stack trace functionality in java.


## Normal Trace
The demo main function is

```java
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
}
```    

The result stack trace is 

```bash
Dump Long Stack Trace
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.dumpStack(LongStackTraceExecutor.java:63)
	at idv.popcorny.lst.Demo.lambda$null$0(Demo.java:18)
	at idv.popcorny.lst.Demo$$Lambda$3/2080575770.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:56)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Async >>>>>>>
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:27)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.Demo.lambda$null$1(Demo.java:17)
	at idv.popcorny.lst.Demo$$Lambda$2/1702289086.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:56)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Async >>>>>>>
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:27)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.Demo.lambda$main$2(Demo.java:16)
	at idv.popcorny.lst.Demo$$Lambda$1/1915318863.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:56)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Async >>>>>>>
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:27)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.Demo.main(Demo.java:15)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:483)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)
```

## Exceptional Case
The main function is

```java
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
```

The result stack trace is 

```bash
java.util.concurrent.CompletionException: java.lang.RuntimeException: java.lang.NullPointerException: Just Testing
	at java.util.concurrent.CompletableFuture.internalComplete(CompletableFuture.java:205)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:459)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:59)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
Caused by: java.lang.RuntimeException: java.lang.NullPointerException: Just Testing
	at asyncing...(Unknown Source)
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:26)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.DemoException.lambda$null$2(DemoException.java:19)
	at idv.popcorny.lst.DemoException$$Lambda$2/56441524.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:59)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
	at asyncing...(Unknown Source)
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:26)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.DemoException.lambda$main$3(DemoException.java:18)
	at idv.popcorny.lst.DemoException$$Lambda$1/1915318863.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:59)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
	at asyncing...(Unknown Source)
	at java.lang.Thread.getStackTrace(Thread.java:1552)
	at idv.popcorny.lst.LongStackTraceExecutor.execute(LongStackTraceExecutor.java:26)
	at java.util.concurrent.CompletableFuture.execAsync(CompletableFuture.java:441)
	at java.util.concurrent.CompletableFuture.runAsync(CompletableFuture.java:2184)
	at idv.popcorny.lst.DemoException.main(DemoException.java:17)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:483)
	at com.intellij.rt.execution.application.AppMain.main(AppMain.java:134)
Caused by: java.lang.NullPointerException: Just Testing
	at idv.popcorny.lst.DemoException.lambda$null$0(DemoException.java:20)
	at idv.popcorny.lst.DemoException$$Lambda$3/103976578.run(Unknown Source)
	at idv.popcorny.lst.LongStackTraceExecutor.lambda$tryThrow$4(LongStackTraceExecutor.java:73)
	at idv.popcorny.lst.LongStackTraceExecutor$$Lambda$4/214623730.run(Unknown Source)
	at java.util.concurrent.CompletableFuture$AsyncRun.exec(CompletableFuture.java:454)
	at java.util.concurrent.CompletableFuture$Async.run(CompletableFuture.java:428)
	at idv.popcorny.lst.LongStackTraceExecutor$LSTTask.run(LongStackTraceExecutor.java:59)
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1142)
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:617)
	at java.lang.Thread.run(Thread.java:745)
```