package net.mcreator.administratorauthorization.eventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EventBusSubscriber
public class ServerTickEvent {

    private static final Map<ServerPlayer, Float> healthMap = new HashMap<>();

    @SubscribeEvent
    public static void tickStart(net.neoforged.neoforge.event.tick.ServerTickEvent.Pre event) {
        List<ServerPlayer> admins = event.getServer().getPlayerList().getPlayers().stream().filter(serverPlayer -> ((EntityAccess) serverPlayer).administrator_authorization$getAuthorization()).toList();
        admins.iterator().forEachRemaining(admin -> {
            if (!(admin.getHealth() > 0.0f) && admin instanceof LivingEntityAccess living) {
                living.administrator_authorization$setHealth(living.administrator_authorization$getFixedMaxHealth());
                ((EntityDataAccess) admin.getEntityData()).administrator_authorization$forceSet(
                        living.administrator_authorization$getAccessorHealth(),
                        living.administrator_authorization$getFixedMaxHealth()
                );
            }
            admin.deathTime = 0;
            healthMap.put(admin, admin.getHealth());
        });
    }

    @SubscribeEvent
    public static void tickEnd(net.neoforged.neoforge.event.tick.ServerTickEvent.Post event) {
        for (Map.Entry<ServerPlayer, Float> healthData : healthMap.entrySet()) {
            ServerPlayer tickedPlayer = event.getServer().getPlayerList().getPlayer(healthData.getKey().getUUID());
            if (tickedPlayer != null && tickedPlayer.getHealth() < healthData.getValue() && tickedPlayer instanceof LivingEntityAccess living) {
                AdministratorAuthorizationMod.LOGGER.warn("Admin Hurt!");
                living.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 1024);
                living.administrator_authorization$setHealth(living.administrator_authorization$getFixedMaxHealth());
                ((EntityDataAccess) tickedPlayer.getEntityData()).administrator_authorization$forceSet(
                        living.administrator_authorization$getAccessorHealth(),
                        1024.0F
                );
                ((EntityAccess) living).administrator_authorization$setEmergency(
                        true
                );
            }
        }
        healthMap.clear();
    }
}
