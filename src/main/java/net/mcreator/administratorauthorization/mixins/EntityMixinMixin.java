package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixinMixin implements EntityAccess {
    @Unique
    private boolean administrator_authorization$Authorized = false;
    @Unique
    private boolean administrator_authorization$authorizeSwitch = true;

    @Override
    public boolean administrator_authorization$getAuthorization() {
        if((Object)this instanceof Player)
                return this.administrator_authorization$Authorized && this.administrator_authorization$authorizeSwitch;
        return false;
    }

    @Override
    public boolean administrator_authorization$getSwitch() {
        return this.administrator_authorization$authorizeSwitch;
    }

    @Override
    public void administrator_authorization$setAuthorization() {
        this.administrator_authorization$Authorized = (Object)this instanceof Player;
    }

    @Override
    public void administrator_authorization$setSwitch(boolean select) {
        this.administrator_authorization$authorizeSwitch = select;
    }

    @Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
    public void isInvulnerableTo(DamageSource p_20122_, CallbackInfoReturnable<Boolean> cir){
        if(this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(true);
        }
    }

}
