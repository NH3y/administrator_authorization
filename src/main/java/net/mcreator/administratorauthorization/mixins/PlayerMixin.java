package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.classes.PlayerRouter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = Integer.MIN_VALUE)
public abstract class PlayerMixin extends LivingEntity implements PlayerAccess {
    @Shadow
    @Final
    private Inventory inventory;
    @Unique
    private boolean administrator_authorization$pressAlter = false;

    @Unique
    private final PlayerRouter administrator_authorization$router = new PlayerRouter((Player) (Object) this);

    @Unique
    private int administrator_authorization$RDSlot = Integer.MAX_VALUE;

    protected PlayerMixin(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
            AdministratorAuthorizationMod.LOGGER.info("Mixin : Hurt - Player");
        }
    }

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    public void hurtArmor(DamageSource p_36251_, float p_36252_, CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    public void disableShield(CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "animateHurt", at = @At("HEAD"), cancellable = true)
    public void animateHurt(float pYaw, CallbackInfo ci) {
        if (((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    public void actuallyHurt(DamageSource pDamageSrc, float pDamageAmount, CallbackInfo ci) {
        if (((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            AdministratorAuthorizationMod.LOGGER.info("Mixin : Actually Hurt");
            ci.cancel();
        }
    }

    @Inject(method = "isImmobile", at = @At("RETURN"), cancellable = true)
    public void isImmobile(CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            cir.setReturnValue(this.isSleeping());
        }
    }

    @Inject(method = "getDigSpeed", at = @At("HEAD"),  cancellable = true)
    public void getDigSpeed(BlockState p_36282_, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            float f = this.inventory.getDestroySpeed(p_36282_);
            if (f > 1.0F) {
                f += (float)this.getAttributeValue(Attributes.MINING_EFFICIENCY);
            }

            if (MobEffectUtil.hasDigSpeed(this)) {
                f *= 1.0F + (float)(MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2F;
            }

            float f1 = f;
            f = net.neoforged.neoforge.event.EventHooks.getBreakSpeed((Player)(Object) this, p_36282_, f, pos);
            if (f1 > f) {
                cir.setReturnValue(f1);
            }
            cir.setReturnValue(f);
        }
    }

    @Override
    public boolean administrator_authorization$isPressAlter() {
        return this.administrator_authorization$pressAlter;
    }

    @Override
    public void administrator_authorization$setPressAlter(boolean administrator_authorization$pressAlter) {
        this.administrator_authorization$pressAlter = administrator_authorization$pressAlter;
    }

    @Override
    public PlayerRouter administrator_authorization$getRouter() {
        return administrator_authorization$router;
    }

    @Override
    public int administrator_authorization$getRDSlot() {
        return administrator_authorization$RDSlot;
    }

    @Override
    public void administrator_authorization$setRDSlot(int administrator_authorization$RDSlot) {
        this.administrator_authorization$RDSlot = administrator_authorization$RDSlot;
    }
}
