package net.mcreator.administratorauthorization.EventTrackers;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent.Applicable;

import java.util.Objects;
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

    @SubscribeEvent
    public static void command(CommandEvent event) throws CommandSyntaxException {
        CommandContextBuilder<CommandSourceStack> contextBuilder = event.getParseResults().getContext();
        if(contextBuilder.getSource().isPlayer() && ((EntityAccess) Objects.requireNonNull(contextBuilder.getSource().getPlayer())).administrator_authorization$getAuthorization()){
            return;
        }
        if (contextBuilder.getArguments().get("targets") != null) {
            EntitySelector selector = (EntitySelector) contextBuilder.getArguments().get("targets").getResult();
            boolean contain = selector.findEntities(contextBuilder.getSource()).stream().anyMatch(entity -> ((EntityAccess)entity).administrator_authorization$getAuthorization());
            event.setCanceled(contain);
            if (contain) System.out.println("Contain Administrator!");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void harmfulEffect(Applicable event){
        event.getEffectInstance();
        if (((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization() && isHarmfulEffect(event.getEffectInstance().getEffect())) {
            event.setResult(Event.Result.DENY);
        }
    }

    private static boolean isHarmfulEffect(MobEffect effect){
        return effect.getCategory().equals(MobEffectCategory.HARMFUL);
    }
}
