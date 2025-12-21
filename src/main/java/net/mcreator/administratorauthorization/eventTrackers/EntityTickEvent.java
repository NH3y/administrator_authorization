package net.mcreator.administratorauthorization.eventTrackers;

import net.mcreator.administratorauthorization.Interfaces.*;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.mcreator.administratorauthorization.network.InventoryDataPacket;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber
public class EntityTickEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingTickClient(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!entity.level().isClientSide()) return;
        if (entity instanceof LivingEntity living && HealthDataOperant.getHealthLock(living)) {
            ((LivingEntityAccess) living).administrator_authorization$setHealth(HealthDataOperant.getHealthLimit(living));
        }
        if (entity instanceof PlayerAccess access) {
            if (entity instanceof LocalPlayerAccess local && local.administrator_authorization$isPressRouter()) {
                access.administrator_authorization$getRouter().inputRouter();
            }
            if (AADestroyerConfiguration.KEEP_IN_INVENTORY.get() && ((EntityAccess) entity).administrator_authorization$getAuthorization()) {
                if (access.administrator_authorization$getRDSlot() != Integer.MAX_VALUE && !((Player) entity).getInventory().contains(AdministratorAuthorizationModItems.REALITY_DESTROYER.get().getDefaultInstance())) {
                    Inventory inventory = ((Player) entity).getInventory();
                    ItemStack stack = new ItemStack(AdministratorAuthorizationModItems.REALITY_DESTROYER.get());
                    stack.setCount(1);
                    inventory.items.set(access.administrator_authorization$getRDSlot(),
                            stack.copy()
                    );
                    PacketDistributor.sendToServer(new InventoryDataPacket(access.administrator_authorization$getRDSlot()));
                }
            }
        }

        if (entity instanceof EntityAccess entityAccess && entity instanceof Player player && entity instanceof LivingEntityAccess livingEntityAccess) {
            final boolean protect = entityAccess.administrator_authorization$getAuthorization();
            if (!protect) return;
            livingEntityAccess.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            ((EntityDataAccess) entity.getEntityData()).administrator_authorization$forceSet(
                    livingEntityAccess.administrator_authorization$getAccessorHealth(), livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            player.getFoodData().setFoodLevel(20);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void LivingTickServer(net.neoforged.neoforge.event.tick.EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide()) {
            return;
        }
        if (entity instanceof Player player) {
            int index = player.getData(AttachmentRegistry.INVENTORY_SLOT_DATA).getSlotIndex();
            if (index != Integer.MAX_VALUE && !player.getInventory().contains(AdministratorAuthorizationModItems.REALITY_DESTROYER.get().getDefaultInstance())) {
                player.getInventory().setItem(index, new ItemStack(
                        AdministratorAuthorizationModItems.REALITY_DESTROYER.get()
                ).copyWithCount(1));
            }
        }
        if (entity instanceof EntityAccess entityAccess && entity instanceof Player player && entity instanceof LivingEntityAccess livingEntityAccess) {
            final boolean protect = entityAccess.administrator_authorization$getAuthorization();
            if (!protect) return;
            livingEntityAccess.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            ((EntityDataAccess) entity.getEntityData()).administrator_authorization$forceSet(
                    livingEntityAccess.administrator_authorization$getAccessorHealth(), livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            player.getFoodData().setFoodLevel(20);
            if (AAAuthorizationConfiguration.RECORD_DEATH_POS.get()) {
                ((ServerPlayer) player).setRespawnPosition(
                        player.level().dimension(),
                        player.blockPosition(),
                        player.bob,
                        true,
                        false
                );
            }
        }
    }
}
