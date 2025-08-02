package net.mcreator.administratorauthorization.mixins;

import com.mojang.authlib.GameProfile;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayer.class, priority = Integer.MIN_VALUE)
public abstract class ServerPlayerMixin extends Player {

    public ServerPlayerMixin(Level pLevel, BlockPos pPos, float pYRot, GameProfile pGameProfile) {
        super(pLevel, pPos, pYRot, pGameProfile);
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource pCause, CallbackInfo ci){
        if(((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir){
        if(((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "indicateDamage", at = @At("HEAD"), cancellable = true)
    public void indicateDamage(double pXDistance, double pZDistance, CallbackInfo ci){
        if(((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Inject(method = "isInvulnerableTo", at = @At("HEAD"), cancellable = true)
    public void isInvulnerableTo(DamageSource pSource, CallbackInfoReturnable<Boolean> cir){
        if(((Object) this) instanceof EntityAccess access && access.administrator_authorization$getAuthorization()){
            cir.setReturnValue(true);
        }
    }
}
