package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SynchedEntityData.DataItem.class, priority = Integer.MAX_VALUE)
public class DataItemMixin<T> implements net.mcreator.administratorauthorization.Interfaces.DataItemAccess {
    @Shadow private boolean dirty;

    @Shadow T value;

    @Unique
    private boolean administrator_authorization$isProtected = false;

    @Unique
    private T administrator_authorization$lockValue;

    @Override
    public void administrator_authorization$setProtected(boolean value){
        administrator_authorization$isProtected = value;
        this.administrator_authorization$lockValue = this.value;
        AdministratorAuthorizationMod.LOGGER.info("data protected");
    }

    @Inject(method = "setDirty", at = @At("HEAD"), cancellable = true)
    public void setDirty(boolean pDirty, CallbackInfo ci){
        if(administrator_authorization$isProtected) {
            ci.cancel();
            this.dirty = false;
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    public void setValue(T pValue, CallbackInfo ci){
        if(this.administrator_authorization$isProtected){
            this.value = administrator_authorization$lockValue;
            ci.cancel();
        }
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    public void getValue(CallbackInfoReturnable<T> cir){
        if(this.administrator_authorization$isProtected){
            cir.setReturnValue(administrator_authorization$lockValue);
        }
    }
}
