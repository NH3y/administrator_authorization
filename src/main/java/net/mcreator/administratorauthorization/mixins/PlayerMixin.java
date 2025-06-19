package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.procedures.MouseDetect;
import net.mcreator.administratorauthorization.procedures.RouterDataOperant;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin implements PlayerAccess {
    @Unique
    private boolean administrator_authorization$pressAlter = false;

    @Unique
    private boolean administrator_authorization$pressRouter = false;

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource p_36154_, float p_36155_, CallbackInfoReturnable<Boolean> cir){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
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

    @Override
    public boolean administrator_authorization$isPressAlter() {
        return this.administrator_authorization$pressAlter;
    }

    @Override
    public void administrator_authorization$setPressAlter(boolean administrator_authorization$pressAlter) {
        this.administrator_authorization$pressAlter = administrator_authorization$pressAlter;
        if((Object)this instanceof Player player) {
            RouterDataOperant.updatePlayerRouterIndex(player, 0);
        }
    }

    @Override
    public boolean administrator_authorization$isPressRouter(){
        return this.administrator_authorization$pressRouter;
    }

    @Override
    public void administrator_authorization$setPressRouter(boolean press) {
        if ((Object) this instanceof Player player) {
            if (press == this.administrator_authorization$pressRouter) return;
            this.administrator_authorization$pressRouter = press;
            if (this instanceof LocalPlayerAccess access) {
                System.out.println("ACCESS MOUSE");
                //try (Minecraft minecraft = access.administrator_authorization$getMinecraft()) {
                //    MouseHandler mouse = minecraft.mouseHandler;
                //    if (press && mouse.isMouseGrabbed()) {
                //        mouse.releaseMouse();
                //    } else if (!mouse.isMouseGrabbed()) {
                //        RouterDataOperant.updatePlayerRouterIndex(player,
                //                1 + MouseDetect.mouseDistrict(((LocalPlayerAccess) this).administrator_authorization$getMinecraft()));
                //        mouse.grabMouse();
                //    }
                //}
            }
        }
    }
}
