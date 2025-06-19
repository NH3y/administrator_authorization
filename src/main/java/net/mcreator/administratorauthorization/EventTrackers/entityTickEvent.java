package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.procedures.MouseDetect;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class entityTickEvent {
    @SubscribeEvent
    public static void LivingTick(LivingTickEvent event){
        Entity entity = event.getEntity();
        if(!entity.level().isClientSide()) return;
        if (entity instanceof EntityAccess entityAccess && entity instanceof Player && entity instanceof LivingEntityAccess livingEntityAccess){
            final boolean protect = entityAccess.administrator_authorization$getAuthorization();
            if(!protect) return;
            livingEntityAccess.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, livingEntityAccess.administrator_authorization$getFixedMaxHealth());
            ((LivingEntity)entity).setHealth(livingEntityAccess.administrator_authorization$getFixedMaxHealth());
        }
    }
}
