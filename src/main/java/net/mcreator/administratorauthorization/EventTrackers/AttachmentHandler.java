package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber
public class AttachmentHandler {
    @SubscribeEvent
    public static void clone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            transmitTo(event.getOriginal(), serverPlayer);
        }
    }

    private static void transmitTo(Player source, ServerPlayer serverPlayer) {
        serverPlayer.getData(AttachmentRegistry.INVENTORY_SLOT_DATA).setSlotIndex(source.getData(AttachmentRegistry.INVENTORY_SLOT_DATA).getSlotIndex());
        serverPlayer.getData(AttachmentRegistry.ROUTER_DATA).setRouterIndex(source.getData(AttachmentRegistry.ROUTER_DATA).getRouterIndex());
        serverPlayer.getData(AttachmentRegistry.HEALTH_DATA).setHealthLock(source.getData(AttachmentRegistry.HEALTH_DATA).isHealthLock());
        serverPlayer.getData(AttachmentRegistry.HEALTH_DATA).setHealthLimit(source.getData(AttachmentRegistry.HEALTH_DATA).getHealthLimit());
    }
}
