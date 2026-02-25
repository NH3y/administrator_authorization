package net.mcreator.administratorauthorization.security;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

public class MonitoringService {
    public static final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    private static final Logger log = LoggerFactory.getLogger(MonitoringService.class);
    public static final List<ScheduledFuture<?>> monitoringTasks = new CopyOnWriteArrayList<>();
    public static final List<Thread> monitoringThreads = new CopyOnWriteArrayList<>();

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(
            1,
            new ThreadFactoryBuilder()
                    .setNameFormat("monitor-%d")
                    .setDaemon(true)
                    .setUncaughtExceptionHandler(((t, e) -> log.error(
                            "Monitor thread {} fail", t.getName(), e
                    )))
                    .build()
    );

    static {

    }

    public void shutdown() {
        monitoringTasks.forEach(task -> task.cancel(false));
        monitoringThreads.forEach(Thread::interrupt);
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @SuppressWarnings("EmptyMethod")
    public static void init() {

    }
}
