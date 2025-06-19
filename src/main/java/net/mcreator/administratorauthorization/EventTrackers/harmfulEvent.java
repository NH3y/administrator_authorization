package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

@Mod.EventBusSubscriber
public class harmfulEvent {
    @SubscribeEvent
    public static void hurt(LivingHurtEvent event){
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if(protect){
            event.setAmount(0.0F);
            event.setCanceled(protect);
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH,20);
        }
    }
    @SubscribeEvent
    public static void death(LivingDeathEvent event){
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if(protect){
            event.setCanceled(protect);
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH,20);
        }
    }
}
