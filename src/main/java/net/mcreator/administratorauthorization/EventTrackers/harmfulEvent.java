package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.logging.Logger;

@Mod.EventBusSubscriber
public class harmfulEvent {
    static Logger logger = Logger.getLogger("Event_Harmful");

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void hurt(LivingHurtEvent event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setAmount(0.0F);
            event.setCanceled(protect);
            logger.info("Block Hurt");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void death(LivingDeathEvent event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setCanceled(protect);
            logger.info("Block Death");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void damage(LivingDamageEvent event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setAmount(0.0F);
            event.setCanceled(protect);
            logger.info("Block Damage");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void knockBack(LivingKnockBackEvent event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setCanceled(protect);
            logger.info("Block Knockback");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }
}
