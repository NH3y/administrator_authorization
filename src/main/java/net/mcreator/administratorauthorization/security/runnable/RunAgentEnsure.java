package net.mcreator.administratorauthorization.security.runnable;

import net.mcreator.administratorauthorization.security.MonitoringService;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RunAgentEnsure implements Runnable {
    private final Set<String> knownAgents = ConcurrentHashMap.newKeySet();

    @Override
    public void run() {
        detectNewAgents();

        if (knownAgents.isEmpty()) {
            return;
        }
        try {
            StringBuilder builder = new StringBuilder("Known Agents: ");
            knownAgents.forEach((agent) -> builder.append(agent).append(", "));
            MonitoringService.queue.put(builder.toString());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void detectNewAgents() {
        // Use JMX to inspect loaded agents
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            ObjectName runtimeName = new ObjectName(
                    "java.lang:type=Runtime");
            String[] inputArgs = (String[]) mbs.getAttribute(
                    runtimeName, "InputArguments");
            for (String arg : inputArgs) {
                if (arg.startsWith("-javaagent:")) {
                    String agentPath = arg.substring(11);
                    if (!knownAgents.contains(agentPath)) {
                        // NEW AGENT DETECTED!
                        handleMaliciousAgent(agentPath);
                    }
                }
            }
        } catch (Exception e) {
            // Log error
        }
    }

    private void handleMaliciousAgent(String agentPath) {
        System.err.println("[SECURITY] Unauthorized agent detected: "
                + agentPath);
        // Emergency response
        // 1. Disable mod loading
        // 2. Alert player
        // 3. Save game state
        // 4. Log incident
        // 5. Optionally: exit game
    }
}
