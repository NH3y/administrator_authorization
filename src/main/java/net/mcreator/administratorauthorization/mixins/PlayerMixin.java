package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.classes.PlayerRouter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, priority = Integer.MAX_VALUE)
public abstract class PlayerMixin implements PlayerAccess {
    @Unique
    private boolean administrator_authorization$pressAlter = false;

    @Unique
    private PlayerRouter administrator_authorization$router = new PlayerRouter((Player)(Object)this);

    @Unique
    private int administrator_authorization$RDSlot = Integer.MAX_VALUE;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
            AdministratorAuthorizationMod.LOGGER.info("Mixin : Hurt");
        }
    }

    @Inject(method = "hurtArmor", at = @At("HEAD"), cancellable = true)
    public void hurtArmor(DamageSource p_36251_, float p_36252_, CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Inject(method = "disableShield", at = @At("HEAD"), cancellable = true)
    public void disableShield(boolean p_36385_, CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(Entity.RemovalReason pReason, CallbackInfo ci) {
        if (pReason.name().equals(Entity.RemovalReason.KILLED.name())){
            ci.cancel();
            AdministratorAuthorizationMod.LOGGER.info("Mixin : Remove");
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
