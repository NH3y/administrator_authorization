package net.mcreator.administratorauthorization.security;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Detector {
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startAttachMonitoring() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Path attachDir = Paths.get(tmpDir, ".java_pid" + ProcessHandle.current().pid());

        scheduler.scheduleAtFixedRate(() -> {
            if (Files.exists(attachDir)) {
                System.err.println("WARNING: Attach API socket detected at: " + attachDir);
                auditAttachDirectory(attachDir);
            }
        }, 0, 5, TimeUnit.SECONDS);
    }

    private static void auditAttachDirectory(Path attachDir) {
        try {
            Files.list(attachDir).forEach(file -> {
                System.err.println("  Suspicious attach file: " + file.getFileName());
            });
        } catch (Exception e) {
            System.err.println("Error auditing attach directory: " + e.getMessage());
        }
    }

    public static void shutdown() {
        scheduler.shutdown();
    }
}
