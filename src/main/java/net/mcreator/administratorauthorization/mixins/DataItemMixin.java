package net.mcreator.administratorauthorization.mixins;

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
    private T administrator_authorization$lockValue;

    @Unique
    private String administrator_authorization$name;

    @Unique
    private int administrator_authorization$lock = 0;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityDataAccessor<T> pAccessor, T pValue, CallbackInfo ci) {
        this.administrator_authorization$name = pAccessor instanceof EntityDataAccessorsAccess access ? access.administrator_authorization$catchName() : "None";
        this.administrator_authorization$container = new VarContainer<>(pValue, administrator_authorization$name + pAccessor.getId());
    }

    @Inject(method = "setValue", at = @At("HEAD"))
    public void setValue(T pValue, CallbackInfo ci) {
        this.administrator_authorization$container.set(pValue);
        if (administrator_authorization$lock == 4 || administrator_authorization$lock == 3) {
            ;administrator_authorization$lockValue = administrator_authorization$container.getPoint();
        }
    }

    @Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
    public void getValue(CallbackInfoReturnable<T> cir) {
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
                    case 4 -> "BEGIN_POINT";
                    default -> "FULL_ACCESS";
                }
        );
        administrator_authorization$lock = level;
        if (level == 4) {
            this.administrator_authorization$lockValue = this.value;
        }
    }

    @Override
    public int administrator_authorization$getLock() {
        return administrator_authorization$lock;
    }

    @Override
    public T administrator_authorization$getLockValue() {
        return administrator_authorization$lockValue;
    }

    @Override
    public void administrator_authorization$setLockValue(T value) {
        this.administrator_authorization$lockValue = value;
        administrator_authorization$container.setPoint(value);
    }
}
