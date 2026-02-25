package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.classes.Vault;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEvent {
    @SubscribeEvent
    public static void start(ServerAboutToStartEvent event) {
        Vault.load();
    }

    @SubscribeEvent
    public static void stop(ServerStoppingEvent event) {
        Vault.save();
    }
}
