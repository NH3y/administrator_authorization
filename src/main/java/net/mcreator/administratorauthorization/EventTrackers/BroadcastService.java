package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.security.MonitoringService;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class BroadcastService {
    @SubscribeEvent
    public static void broadcast(TickEvent.ServerTickEvent event) {
        if (event.getServer().getTickCount() % 60 == 0) {
            List<String> results = new ArrayList<>();
            MonitoringService.queue.drainTo(results);
            if (results.isEmpty()) {
                return;
            }
            for (String s : results) {
                System.out.println(s);
            }
        }
    }
}
