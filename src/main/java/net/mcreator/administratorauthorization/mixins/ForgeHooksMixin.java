package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ForgeHooks.class, remap = false)
public class ForgeHooksMixin {

    @Inject(method = "onLivingDeath", at = @At("RETURN"), cancellable = true)
    private static void onLivingDeath(LivingEntity entity, DamageSource src, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "onLivingDamage", at = @At("RETURN"), cancellable = true)
    private static void onLivingDamage(LivingEntity entity, DamageSource src, float amount, CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()){
            cir.setReturnValue(0.0f);
        }
    }

    @Inject(method = "onLivingHurt", at = @At("RETURN"), cancellable = true)
    private static void onLivingHurt(LivingEntity entity, DamageSource src, float amount, CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()){
            cir.setReturnValue(0.0f);
        }
    }
    @Inject(method = "onLivingAttack", at = @At("RETURN"), cancellable = true)
    private static void onLivingAttack(LivingEntity entity, DamageSource src, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) entity).administrator_authorization$getAuthorization()){
            cir.setReturnValue(false);
        }
    }
}
