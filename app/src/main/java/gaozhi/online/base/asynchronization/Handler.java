package gaozhi.online.base.asynchronization;

import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

/**
 * 重写Handler
 */
public class Handler extends android.os.Handler {
    /**
     * 处理者
     */
    public interface Worker {
        void handleMessage(@NonNull Message msg);
    }

    private Worker worker;

    public Handler() {
        super(Looper.myLooper());
    }

    public Handler(Worker worker) {
        super(Looper.myLooper());
        this.worker = worker;
    }

    public Handler(Worker worker, Looper looper) {
        super(looper);
        this.worker = worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        if (worker != null) {
            worker.handleMessage(msg);
        }
    }
}
