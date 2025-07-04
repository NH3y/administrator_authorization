package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LocalPlayer.class, priority = Integer.MAX_VALUE)
public abstract class LocalPlayerMixin implements net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess {
    @Shadow @Final protected Minecraft minecraft;

    @Shadow public abstract void setShowDeathScreen(boolean pShow);

    @Override
    public Minecraft administrator_authorization$getMinecraft(){
        return this.minecraft;
    }

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    public void tickDeath(CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
            AdministratorAuthorizationMod.LOGGER.info("Mixin : tickDeath");
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
}
