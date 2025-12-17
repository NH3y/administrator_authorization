package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingKnockBackEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommonHooks.class, remap = false)
public class CommonHooksMixin {

    @Inject(method = "onLivingDeath", at = @At("RETURN"), cancellable = true)
    private static void onLivingDeath(LivingEntity entity, DamageSource src, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isEntityInvulnerableTo", at = @At("RETURN"), cancellable = true)
    private static void isEntityInvulnerableTo(Entity entity, DamageSource source, boolean isInvul, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onEntityIncomingDamage", at = @At("RETURN"), cancellable = true)
    private static void onLivingDamage(LivingEntity entity, DamageContainer container, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onLivingKnockBack", at = @At("RETURN"), cancellable = true)
    private static void onLivingKnockBack(LivingEntity target, float strength, double ratioX, double ratioZ, CallbackInfoReturnable<LivingKnockBackEvent> cir) {
        if (((EntityAccess) target).administrator_authorization$getAuthorization()) {
            LivingKnockBackEvent event = cir.getReturnValue();
            event.setCanceled(true);
            event.setStrength(0.0F);
            cir.setReturnValue(event);
        }
    }
}
