package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;

import java.util.HashSet;
import java.util.UUID;
import java.util.logging.Logger;

@Mod.EventBusSubscriber
public class playerShift {
    static Logger logger = Logger.getLogger("Event_Clone");
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPlayerClone(Clone event){
        event.getOriginal().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(oldData -> event.getEntity().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(newData -> newData.setRouterIndex(oldData.getRouterIndex())));
        final boolean protect = ((EntityAccess) event.getOriginal()).administrator_authorization$getAuthorization();
        if(protect){
            if(event.isWasDeath()){
                Player resource = event.getOriginal();
                Player brandNew = event.getEntity();
                brandNew.getInventory().replaceWith(resource.getInventory());
                if(brandNew instanceof ServerPlayer serverPlayer){
                    ((EntityAccess) serverPlayer).administrator_authorization$setAuthorization();
                    serverPlayer.restoreFrom(((ServerPlayer) resource), true);
                    serverPlayer.setXRot(resource.getXRot());
                    serverPlayer.setYRot(resource.getYRot());
                    logger.info("Server Player died");
                }else if (brandNew instanceof LocalPlayer localPlayer){
                    ((EntityAccess) localPlayer).administrator_authorization$setAuthorization();
                    localPlayer.restoreFrom(resource);
                    localPlayer.teleportTo(
                            (ServerLevel) resource.level(),
                            resource.position().x(),
                            resource.position().y(),
                            resource.position().z(),
                            new HashSet<>(),
                            resource.getYRot(),
                            resource.getXRot()
                            );
                    logger.info("Local Player Died");
                }

            }
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH,20);
        }
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        MinecraftServer server = player.server;

        UUID playerUUID = player.getUUID();

        for(ServerLevel level : server.getAllLevels()){
            Entity existing = level.getEntity(playerUUID);
            if(existing != player && existing instanceof ServerPlayer duplicatePlayer){
                if(duplicatePlayer.isRemoved()){
                    level.getPlayers(playerPredicate -> playerPredicate.is(existing)).forEach(serverPlayer -> serverPlayer.remove(Entity.RemovalReason.DISCARDED));
                }
            }
        }
    }

}
