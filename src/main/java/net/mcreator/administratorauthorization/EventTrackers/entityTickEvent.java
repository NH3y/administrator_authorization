package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.*;
import net.mcreator.administratorauthorization.capabilities.InventoryDataProvider;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.network.InventoryDataPacket;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class entityTickEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingTickClient(LivingTickEvent event){
        Entity entity = event.getEntity();
        if(!entity.level().isClientSide()) return;
        if(entity instanceof LivingEntity living && HealthDataOperant.getHealthLock(living)){
            ((LivingEntityAccess) living).administrator_authorization$setHealth(HealthDataOperant.getHealthLimit(living));
        }
        if(entity instanceof PlayerAccess access){
            if(((LocalPlayerAccess) entity).administrator_authorization$isPressRouter()) {
                access.administrator_authorization$getRouter().inputRouter();
            }
            if(AADestroyerConfiguration.KEEP_IN_INVENTORY.get()){
                if(access.administrator_authorization$getRDSlot() != Integer.MAX_VALUE && !((Player)entity).getInventory().contains(AdministratorAuthorizationModItems.REALITY_DESTROYER.get().getDefaultInstance())){
                    Inventory inventory = ((Player) entity).getInventory();
                    ItemStack stack = new ItemStack(AdministratorAuthorizationModItems.REALITY_DESTROYER.get());
                    stack.setCount(1);
                    inventory.items.set(access.administrator_authorization$getRDSlot(),
                            stack.copy()
                            );
                    AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new InventoryDataPacket(access.administrator_authorization$getRDSlot()));
                }
            }
        }

        if (entity instanceof EntityAccess entityAccess && entity instanceof Player && entity instanceof LivingEntityAccess livingEntityAccess){
            final boolean protect = entityAccess.administrator_authorization$getAuthorization();
            if(!protect) return;
            livingEntityAccess.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            ((LivingEntity)entity).setHealth(livingEntityAccess.administrator_authorization$getFixedMaxHealth());
        }
    }

    @SubscribeEvent
    public static void LivingTickServer(LivingTickEvent event){
        Entity entity = event.getEntity();
        if(entity.level().isClientSide()){
            return;
        }
        if(entity instanceof Player player){
            LazyOptional<IInventoryData> optional = player.getCapability(InventoryDataProvider.SLOT_DATA);
            if(!optional.isPresent()) return;
            int index = optional.map(IInventoryData::getSlotIndex).orElse(Integer.MAX_VALUE);
            if(index != Integer.MAX_VALUE && !player.getInventory().contains(AdministratorAuthorizationModItems.REALITY_DESTROYER.get().getDefaultInstance())){
                player.getInventory().setItem(index, new ItemStack(
                        AdministratorAuthorizationModItems.REALITY_DESTROYER.get()
                ).copyWithCount(1));
            }
        }
    }
}
