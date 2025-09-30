package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.DataItemAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccessorsAccess;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SynchedEntityData.DataItem.class, priority = Integer.MIN_VALUE)
public class DataItemMixin<T> implements DataItemAccess {
    @Shadow private boolean dirty;

    @Shadow T value;

    @Shadow
    @Final
    EntityDataAccessor<T> accessor;
    @Shadow
    @Final
    private T initialValue;
    @Unique
    T administrator_authorization$f_15446;

    @Unique
    private boolean administrator_authorization$isProtected = false;

    @Unique
    private T administrator_authorization$lockValue;

    @Unique
    private String administrator_authorization$name;

    @Override
    public void administrator_authorization$setProtected(boolean value){
        administrator_authorization$isProtected = value;
        this.administrator_authorization$lockValue = this.value;
        AdministratorAuthorizationMod.LOGGER.info("{}'s data has been protected", this.administrator_authorization$name);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityDataAccessor<T> pAccessor, T pValue, CallbackInfo ci){
        this.administrator_authorization$name = pAccessor instanceof EntityDataAccessorsAccess access ? access.administrator_authorization$catchName() : "None";
        this.administrator_authorization$f_15446 = pValue;
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
        this.administrator_authorization$f_15446 = pValue;
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    public void getValue(CallbackInfoReturnable<T> cir){
        if(this.administrator_authorization$isProtected){
            cir.setReturnValue(administrator_authorization$lockValue);
        }
        cir.setReturnValue(this.administrator_authorization$f_15446);
    }

    @Inject(method = "value", at = @At("RETURN"), cancellable = true)
    public void value(CallbackInfoReturnable<SynchedEntityData.DataValue<T>> cir){
        cir.setReturnValue(SynchedEntityData.DataValue.create(
                this.accessor,
                this.administrator_authorization$f_15446
                )
        );
    }

    @Inject(method = "isSetToDefault", at = @At("RETURN"), cancellable = true)
    public void isSetToDefault(CallbackInfoReturnable<Boolean> cir){
        cir.setReturnValue(
                this.initialValue.equals(this.administrator_authorization$f_15446)
        );
    }
}
