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
        List<StackTraceElement> frames = new ArrayList<>();

        // Add the current stack frames
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        frames.add(new StackTraceElement("asyncing", "..", null, 0));
        for (StackTraceElement stackTraceElement : stackTrace) {
            frames.add(stackTraceElement);
        }


        LSTTask lstTask = lstTaskThreadLocal.get();
        if(lstTask != null) {
            for (StackTraceElement frame : lstTask.frames) {
                frames.add(frame);
            }
        }

        executor.execute(
                new LSTTask(command, frames.toArray(new StackTraceElement[0]))
        );
    }


    class LSTTask implements Runnable {
        private Runnable runnable;
        StackTraceElement[] frames;

        LSTTask(Runnable runnable, StackTraceElement[] frames) {
            this.runnable = runnable;
            this.frames = frames;
        }

        @Override
        public void run() {
            lstTaskThreadLocal.set(this);
            try {
                runnable.run();
            } catch (Throwable t) {
                RuntimeException e = new RuntimeException(t);
                e.setStackTrace(frames);
                throw e;
            }
            lstTaskThreadLocal.set(null);
        }
    }

    public static Runnable tryThrow(Runnable runnable) {
        return () -> {
            LSTTask lstTask = lstTaskThreadLocal.get();
            try {
                runnable.run();
            } catch (Throwable t) {
                RuntimeException e = new RuntimeException(t);
                e.setStackTrace(lstTask.frames);
                throw e;
            }
        };
    }

    public static void dumpStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        System.err.println("Dump Long Stack Trace");
        for (StackTraceElement stackTraceElement : stackTrace) {
            System.err.println("\tat " + stackTraceElement.toString());
        }

        LSTTask lstTask = lstTaskThreadLocal.get();
        if(lstTask != null) {
            for(StackTraceElement frame : lstTask.frames) {
                if(frame.getClassName().equals("asyncing")) {
                    System.err.println("Async >>>>>>");
                } else {
                    System.err.println("\tat " + frame.toString());
                }
            }
        }
    }

    static class LSTTaskThreadLocal extends  ThreadLocal<LSTTask> {
    }
}
