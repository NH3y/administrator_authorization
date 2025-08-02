package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber
public class serverTickEvent {

    private static final Map<ServerPlayer, Float> healthMap = new HashMap<>();

    @SubscribeEvent
    public static void tickStart(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            List<ServerPlayer> admins = event.getServer().getPlayerList().getPlayers().stream().filter(serverPlayer -> ((EntityAccess) serverPlayer).administrator_authorization$getAuthorization()).toList();
            admins.iterator().forEachRemaining(admin ->{
                if(!(admin.getHealth() > 0.0f) && admin instanceof LivingEntityAccess living){
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
    }

    @SubscribeEvent
    public static void tickEnd(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            for (Map.Entry<ServerPlayer, Float> healthData : healthMap.entrySet()) {
                ServerPlayer tickedPlayer = event.getServer().getPlayerList().getPlayer(healthData.getKey().getUUID());
                if(tickedPlayer != null && tickedPlayer.getHealth() < healthData.getValue() && tickedPlayer instanceof LivingEntityAccess living){
                    AdministratorAuthorizationMod.LOGGER.warn("Admin Hurt!");
                    living.administrator_authorization$setHealth(living.administrator_authorization$getFixedMaxHealth());
                    ((EntityDataAccess) tickedPlayer.getEntityData()).administrator_authorization$forceSet(
                            living.administrator_authorization$getAccessorHealth(),
                            living.administrator_authorization$getFixedMaxHealth()
                    );
                }
                healthMap.remove(tickedPlayer);
            }
        }
    }
}
