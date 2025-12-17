package net.mcreator.administratorauthorization.EventTrackers;

import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModBlocks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.CommandEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;

import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

@EventBusSubscriber
public class harmfulEvent {
    static final Logger logger = Logger.getLogger("Event_Harmful");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void hurt(LivingDamageEvent.Pre event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setNewDamage(0.0F);
            event.getContainer().setNewDamage(0.0F);
            event.getContainer().setPostAttackInvulnerabilityTicks(Integer.MAX_VALUE);
            logger.info("Block Hurt");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void death(LivingDeathEvent event) {
        final boolean protect = ((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setCanceled(protect);
            logger.info("Block Death");
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH, 20);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void attack(AttackEntityEvent event) {
        final boolean protect = ((EntityAccess) event.getTarget()).administrator_authorization$getAuthorization();
        if (protect) {
            event.setCanceled(protect);
            logger.info("Block Attack");
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
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
        if (AAAuthorizationConfiguration.COMMAND_PROTECT.get()) {
            CommandContextBuilder<CommandSourceStack> contextBuilder = event.getParseResults().getContext();
            if (contextBuilder.getSource().isPlayer() && ((EntityAccess) Objects.requireNonNull(contextBuilder.getSource().getPlayer())).administrator_authorization$getAuthorization()) {
                return;
            }
            if (contextBuilder.getArguments().get("targets") != null) {
                EntitySelector selector = (EntitySelector) contextBuilder.getArguments().get("targets").getResult();
                boolean contain = selector.findEntities(contextBuilder.getSource()).stream().anyMatch(entity -> ((EntityAccess) entity).administrator_authorization$getAuthorization());
                event.setCanceled(contain);
                if (contain) System.out.println("Contain Administrator!");
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void harmfulEffect(MobEffectEvent.Applicable event) {
        event.getEffectInstance();
        if (((EntityAccess) event.getEntity()).administrator_authorization$getAuthorization() && isHarmfulEffect(event.getEffectInstance().getEffect().value())) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    private static boolean isHarmfulEffect(MobEffect effect) {
        MobEffectCategory category = effect.getCategory();
        if (AAAuthorizationConfiguration.BAN_NEUTRAL.get()) {
            return category.equals(MobEffectCategory.HARMFUL) || category.equals(MobEffectCategory.NEUTRAL);
        }
        return category.equals(MobEffectCategory.HARMFUL);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void explosion(ExplosionEvent.Start event) {
        for (Map.Entry<Player, Vec3> entry : event.getExplosion().getHitPlayers().entrySet()) {
            if (((EntityAccess) entry.getKey()).administrator_authorization$getAuthorization()) {
                event.setCanceled(true);
                break;
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void breakNothingness(BlockEvent.BreakEvent event) {
        event.setCanceled(event.getState().getBlock().equals(AdministratorAuthorizationModBlocks.NOTHINGNESS.get()));
    }
}
