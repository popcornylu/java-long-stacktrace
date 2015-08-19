package idv.popcorny.lst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by popcorny on 8/19/15.
 */
public class LongStackTraceExecutor implements Executor {

    private Executor executor;
    private static final LSTTaskThreadLocal lstTaskThreadLocal = new LSTTaskThreadLocal();

    public LongStackTraceExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public void execute(Runnable command) {
        List<String> frames = new ArrayList<String>();

        // Add the current stack frames
        frames.add("Async >>>>>>>");
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackTraceElement : stackTrace) {
            frames.add("\tat "+stackTraceElement.toString());
        }


        LSTTask lstTask = lstTaskThreadLocal.get();
        if(lstTask != null) {
            for (String frame : lstTask.frames) {
                frames.add(frame);
            }
        }

        executor.execute(new LSTTask(command, frames));
    }


    class LSTTask implements Runnable {
        private Runnable runnable;
        List<String> frames;

        LSTTask(Runnable runnable, List<String> frames) {
            this.runnable = runnable;
            this.frames = frames;
        }

        @Override
        public void run() {
            lstTaskThreadLocal.set(this);
            runnable.run();
            lstTaskThreadLocal.set(null);
        }

    }

    public static void dumpStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.err.println("Dump Long Stack Trace");
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.err.println("\tat " + stackTraceElement.toString());
        }

        LSTTask lstTask = lstTaskThreadLocal.get();
        if(lstTask != null) {
            for(String frame : lstTask.frames) {
                System.err.println(frame);
            }
        }
    }

    static class LSTTaskThreadLocal extends  ThreadLocal<LSTTask> {
    }
}
