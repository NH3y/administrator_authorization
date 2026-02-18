package net.mcreator.administratorauthorization.security.runnable;

import net.mcreator.administratorauthorization.security.MonitoringService;

public class RunPropertyEnsure implements Runnable {
    @Override
    public void run() {
        while (true) {
            String property = System.getProperty("jdk.attach.allowAttachSelf");
            if (property != null && property.equals("true")) {
                System.setProperty("jdk.attach.allowAttachSelf", "false");
                try {
                    MonitoringService.queue.put("Self Attach Property has been toggled!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
