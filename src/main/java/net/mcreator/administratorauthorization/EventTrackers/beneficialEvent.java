package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class beneficialEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHeal(LivingHealEvent event){
        LivingEntity entity = event.getEntity();
        if (HealthDataOperant.getHealthLock(entity) && HealthDataOperant.getHealthLimit(entity) < entity.getHealth()){
            ((LivingEntityAccess) entity).administrator_authorization$setHealth(HealthDataOperant.getHealthLimit(entity));
            event.setAmount(0);
            event.setCanceled(true);
        }
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if(protect){
            event.setAmount(entity.getMaxHealth() - entity.getHealth());
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH,20);
        }
    }
}
