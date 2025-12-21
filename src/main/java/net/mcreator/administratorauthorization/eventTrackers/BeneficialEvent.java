package net.mcreator.administratorauthorization.eventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;

@EventBusSubscriber
public class BeneficialEvent {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onHeal(LivingHealEvent event) {
        LivingEntity entity = event.getEntity();
        if (HealthDataOperant.getHealthLock(entity) && HealthDataOperant.getHealthLimit(entity) < entity.getHealth()) {
            ((LivingEntityAccess) entity).administrator_authorization$setHealth(HealthDataOperant.getHealthLimit(entity));
            event.setAmount(0);
            event.setCanceled(true);
        }
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setAmount(entity.getMaxHealth() - entity.getHealth());
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }
}
