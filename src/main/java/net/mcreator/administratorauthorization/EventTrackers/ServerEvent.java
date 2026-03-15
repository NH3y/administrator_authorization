package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.classes.TerminalClassFactory;
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
        TerminalClassFactory.getInstance().load();
    }

    @SubscribeEvent
    public static void stop(ServerStoppingEvent event) {
        //printMixinMethods();
        Vault.save();
        TerminalClassFactory.getInstance().save();
    }

    @SuppressWarnings("unused")
    private static void printMixinMethods() {
        Vault.sortedMixinMethods.forEach((key, value) -> {
            System.out.println(key + ": " + value.size());
            value.forEach(val -> {
                System.out.println(val + " (" + val.getDeclaringClass().getSimpleName() + ")");
            });
        });
    }
}
