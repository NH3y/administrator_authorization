package net.mcreator.administratorauthorization.mixins;

import com.mojang.authlib.GameProfile;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(value = LocalPlayer.class, priority = Integer.MIN_VALUE)
public abstract class LocalPlayerMixin extends AbstractClientPlayer implements LocalPlayerAccess {

    public LocalPlayerMixin(ClientLevel pClientLevel, GameProfile pGameProfile) {
        super(pClientLevel, pGameProfile);
    }
    @Shadow @Final protected Minecraft minecraft;

    @Shadow public abstract void setShowDeathScreen(boolean pShow);

    @Unique
    private boolean administrator_authorization$pressRouter = false;

    @Override
    public Minecraft administrator_authorization$getMinecraft(){
        return this.minecraft;
    }

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    public void tickDeath(CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
            AdministratorAuthorizationMod.LOGGER.info("Mixin : tickDeath");
            this.deathTime = 0;
        }
    }
    @Inject(method = "setShowDeathScreen", at = @At("HEAD"), cancellable = true)
    public void setShowDeathScreen(CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            this.setShowDeathScreen(false);
            ci.cancel();
            AdministratorAuthorizationMod.LOGGER.info("Mixin : setShowScreen");
        }
    }

    @Inject(method = "shouldShowDeathScreen", at = @At("RETURN"), cancellable = true)
    public void shouldShowDeathScreen(CallbackInfoReturnable<Boolean> cir) {
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
            AdministratorAuthorizationMod.LOGGER.info("Mixin : getShowScreen");
        }
    }

    @Inject(method = "hurtTo", at = @At("HEAD"), cancellable = true)
    public void hurt(float pHealth, CallbackInfo ci) {
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    public void actuallyHurt(DamageSource pDamageSrc, float pDamageAmount, CallbackInfo ci) {
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "respawn", at = @At("HEAD"), cancellable = true)
    public void respawn(CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Unique
    private boolean administrator_authorization$safeGuard = false;

    @Override
    public void administrator_authorization$setPressRouter(boolean press) {
        if ((Object) this instanceof PlayerAccess player) {
            this.administrator_authorization$pressRouter = press;
            Minecraft minecraft = this.minecraft;
            MouseHandler mouse = minecraft.mouseHandler;

            if (press && mouse.isMouseGrabbed()) {
                mouse.releaseMouse();
            } else if (!mouse.isMouseGrabbed()) {
                player.administrator_authorization$getRouter().updateCapability();
                if(!administrator_authorization$safeGuard) {
                    administrator_authorization$safeGuard = true;
                    mouse.grabMouse();
                }
                administrator_authorization$safeGuard = false;
            }
        }
    }

    @Override
    public boolean administrator_authorization$isPressRouter(){
        return this.administrator_authorization$pressRouter;
    }
}
