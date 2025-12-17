package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.DataItemAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccessorsAccess;
import net.mcreator.administratorauthorization.classes.VarContainer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SynchedEntityData.DataItem.class, priority = Integer.MIN_VALUE)
public abstract class DataItemMixin<T> implements DataItemAccess<T> {
    @Shadow
    private boolean dirty;

    @Shadow
    T value;

    @Shadow
    @Final
    EntityDataAccessor<T> accessor;
    @Shadow
    @Final
    private T initialValue;

    @Shadow
    public abstract void setDirty(boolean pDirty);

    @Unique
    private VarContainer<T> administrator_authorization$container;

    @Unique
    private boolean administrator_authorization$isProtected = false;

    @Unique
    private T administrator_authorization$lockValue;

    @Unique
    private String administrator_authorization$name;

    @Override
    public void administrator_authorization$setProtected(boolean value) {
        administrator_authorization$isProtected = value;
        this.administrator_authorization$lockValue = this.value;
        AdministratorAuthorizationMod.LOGGER.info("{}'s data has been protected", this.administrator_authorization$name);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityDataAccessor<T> pAccessor, T pValue, CallbackInfo ci) {
        //this.administrator_authorization$name = ((EntityDataAccessorsAccess)pAccessor).administrator_authorization$catchName();
        this.administrator_authorization$container = new VarContainer<>(pValue, administrator_authorization$name + pAccessor.id());
    }

    @Inject(method = "setDirty", at = @At("HEAD"), cancellable = true)
    public void setDirty(boolean pDirty, CallbackInfo ci) {
        if (administrator_authorization$isProtected) {
            ci.cancel();
            this.dirty = false;
        }
    }

    @Inject(method = "setValue", at = @At("HEAD"), cancellable = true)
    public void setValue(T pValue, CallbackInfo ci) {
        if (this.administrator_authorization$isProtected) {
            this.value = administrator_authorization$lockValue;
            ci.cancel();
        }
        this.administrator_authorization$container.set(pValue);
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    public void getValue(CallbackInfoReturnable<T> cir) {
        if (this.administrator_authorization$isProtected) {
            cir.setReturnValue(administrator_authorization$lockValue);
        }
        cir.setReturnValue(this.administrator_authorization$container.get());
    }

    @Inject(method = "value", at = @At("RETURN"), cancellable = true)
    public void value(CallbackInfoReturnable<SynchedEntityData.DataValue<T>> cir) {
        cir.setReturnValue(SynchedEntityData.DataValue.create(
                        this.accessor,
                        this.administrator_authorization$container.get()
                )
        );
    }

    @Inject(method = "isSetToDefault", at = @At("RETURN"), cancellable = true)
    public void isSetToDefault(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(
                this.administrator_authorization$container.isDefault()
        );
    }

    @SuppressWarnings("unchecked")
    @Redirect(method = "getValue", at = @At(value = "FIELD", target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;value:Ljava/lang/Object;", opcode = Opcodes.GETFIELD))
    private T toSwapGet(SynchedEntityData.DataItem<T> instance) {
        if (instance instanceof DataItemAccess<?> item) {
            return (T) item.administrator_authorization$directlyInteract(false, null);
        }
        return instance.getValue();
    }

    @SuppressWarnings("unchecked")
    @Redirect(method = "setValue", at = @At(value = "FIELD", target = "Lnet/minecraft/network/syncher/SynchedEntityData$DataItem;value:Ljava/lang/Object;", opcode = Opcodes.PUTFIELD))
    private void toSwapPut(SynchedEntityData.DataItem<T> instance, T value) {
        if (instance instanceof DataItemAccess<?>) {
            ((DataItemAccess<T>) instance).administrator_authorization$directlyInteract(true, value);
        }else {
            this.value = value;
        }
    }

    @Override
    public void administrator_authorization$indexingItem() {
        this.administrator_authorization$container.indexing();
    }

    @Override
    public void administrator_authorization$dirty() {
        this.setDirty(true);
    }

    @Override
    public T administrator_authorization$directlyInteract(boolean toWrite, T value) {
        if (toWrite) {
            this.administrator_authorization$container.set(value);
        }
        return administrator_authorization$container.get();
    }

    @Override
    public void administrator_authorization$toLock(int level) {
        this.administrator_authorization$container.lock(
                switch (level) {
                    case 1 -> "READ_ONLY";
                    case 2 -> "NO_ACCESS";
                    case 3 -> "STOP_POINT";
                    default -> "FULL_ACCESS";
                }
        );
    }
}
