package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public class Destiny {
    private static final Map<Entity, Integer> victims = new HashMap<>();

    @SubscribeEvent
    public static void onTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            victims.forEach((entity, integer) -> {
                entity.kill();
                entity.setInvulnerable(false);
            });
            victims.replaceAll(((entity, integer) -> entity.isAlive() ? integer + 1 : -1));
            if (victims.containsValue(10000000)) {
                Set<Map.Entry<Entity, Integer>> entries = victims.entrySet().stream().filter(entry -> entry.getValue() == 10000000).collect(Collectors.toSet());
                entries.forEach((entry) -> {
                    victims.remove(entry.getKey());
                    AdministratorAuthorizationMod.LOGGER
                            .warn("entity {} still alive", entry.getKey().getId());
                });
            }
            if (victims.containsValue(-1)) {
                Set<Map.Entry<Entity, Integer>> entries = victims.entrySet().stream().filter(entry -> entry.getValue() == -1).collect(Collectors.toSet());
                entries.forEach((entry) -> victims.remove(entry.getKey()));
            }
        }
    }

    public static void addVictim(Entity entity) {
        victims.put(entity, 0);
    }
}
