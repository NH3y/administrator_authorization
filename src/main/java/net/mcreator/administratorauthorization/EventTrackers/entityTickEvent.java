package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class entityTickEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void LivingTick(LivingTickEvent event){
        Entity entity = event.getEntity();
        if(!entity.level().isClientSide()) return;
        if(entity instanceof LivingEntity living && HealthDataOperant.getHealthLock(living)){
            ((LivingEntityAccess) living).administrator_authorization$setHealth(HealthDataOperant.getHealthLimit(living));
        }
        if(entity instanceof PlayerAccess access){
            if(access.administrator_authorization$isPressRouter()) {
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
}
