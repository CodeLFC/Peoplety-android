package gaozhi.online.base.asynchronization;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.MessageQueue;

import java.util.concurrent.Executor;

/**
 * @author Zheng Haibo
 * @Web http://www.mobctrl.net
 */
public final class GlobalExecutor implements Executor {

    static final String APP_EXECUTOR = "APP_EXECUTE";

    private final Handler mainHandler;
    private final Handler backgroundHandler;

    public GlobalExecutor() {
        if (!isOnMainThread()) {
            throw new IllegalStateException(
                    "Error!Please init the Executor in the main thread...");
        }
        mainHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread(APP_EXECUTOR);
        handlerThread.start();
        backgroundHandler = new Handler(handlerThread.getLooper());
    }

    public Handler getMainHandler() {
        return mainHandler;
    }

    public Handler getBackgroundHandler() {
        return backgroundHandler;
    }

    @Override
    public void execute(final Runnable command) {
        executeInBackground(command, 0);
    }

    /**
     * execute the command in background thread with a delay of
     *
     * @param command
     */
    public void execute(final Runnable command, int delay) {
        executeInBackground(command, delay);
    }

    /**
     * execute the command in UiThread
     *
     * @param command
     */
    public void executeInUiThread(final Runnable command) {
        mainHandler.post(command);
    }

    /**
     * execute the command in main thread with a delay of
     *
     * @param command
     */
    public void executeInUiThread(final Runnable command, long delay) {
        mainHandler.postDelayed(command, delay);
    }

    /**
     * execute the command in background thread with a delay of
     *
     * @param command
     */
    public void executeInBackground(final Runnable command, final int delay) {
        if (isOnMainThread()) {
            executeDelayedAfterIdleUnsafe(command, delay);
        } else {
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    executeDelayedAfterIdleUnsafe(command, delay);
                }
            });
        }
    }

    /**
     * execute the command in background thread
     *
     * @param command
     */
    public void executeInBackground(final Runnable command) {
        executeInBackground(command, 0);
    }

    private boolean isOnMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    private void executeDelayedAfterIdleUnsafe(final Runnable task,
                                               final int delay) {
        Looper.myQueue().addIdleHandler(new MessageQueue.IdleHandler() {
            @Override
            public boolean queueIdle() {
                backgroundHandler.postDelayed(task, delay);
                return false;
            }
        });
    }
}
