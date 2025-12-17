package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(value = SynchedEntityData.class, priority = Integer.MIN_VALUE)
public abstract class SynchedEntityDataMixin implements EntityDataAccess {
    @Shadow
    private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> pKey) {
        return null;
    }

    @Shadow
    public abstract <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce);

    @Shadow
    private boolean isDirty;

    @Shadow
    public abstract <T> T get(EntityDataAccessor<T> pKey);

    @Shadow
    @Final
    private SynchedEntityData.DataItem<?>[] itemsById;

    @Shadow
    @Final
    private SyncedDataHolder entity;

    @Unique
    private final Set<Integer> administrator_authorization$bannedId = new HashSet<>(4);

    @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("HEAD"), cancellable = true)
    public <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce, CallbackInfo ci) {
        if (((EntityAccess) this.entity).administrator_authorization$getAuthorization()) {
            if (this.administrator_authorization$bannedId.contains(pKey.id())) {
                ci.cancel();
                AdministratorAuthorizationMod.LOGGER.info("Mixin : setEntityData");
            }
            @SuppressWarnings("unchecked")
            DataItemAccess<T> item = (DataItemAccess<T>) this.getItem(pKey);

            if (item != null && (pForce || ObjectUtils.notEqual(pValue, item.administrator_authorization$directlyInteract(false, null)))) {
                item.administrator_authorization$directlyInteract(true, pValue);
                this.entity.onSyncedDataUpdated(pKey);
                item.administrator_authorization$dirty();
                this.isDirty = true;
            }
            ci.cancel();
        }
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public <T> void get(EntityDataAccessor<T> pKey, CallbackInfoReturnable<T> cir) {
        if (((EntityAccess) this.entity).administrator_authorization$getAuthorization()) {
            @SuppressWarnings("unchecked")
            DataItemAccess<T> item = (DataItemAccess<T>) this.getItem(pKey);

            if (item != null) {
                cir.setReturnValue(
                        item.administrator_authorization$directlyInteract(false, null)
                );
            } else if (this.getItem(pKey) != null){
                //noinspection DataFlowIssue
                cir.setReturnValue(
                        this.getItem(pKey).getValue()
                );
            }
        }
    }

    @Override
    public <T> void administrator_authorization$forceSet(EntityDataAccessor<T> pKey, T pValue) {
        SynchedEntityData.DataItem<T> dataitem = this.getItem(pKey);
        if (dataitem != null) {
            dataitem.setValue(pValue);
            this.entity.onSyncedDataUpdated(pKey);
            dataitem.setDirty(true);
        }
        this.isDirty = true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> SynchedEntityData.DataItem<T> administrator_authorization$publicGetItem(EntityDataAccessor<T> pKey) {
        return (SynchedEntityData.DataItem<T>) this.itemsById[pKey.id()];
    }

    @Override
    public List<SynchedEntityData.DataItem<?>> administrator_authorization$getAllItems() {
        return Arrays.stream(this.itemsById).toList();
    }

    @Override
    public Set<Integer> Administrator_authorization$getBannedId() {
        return administrator_authorization$bannedId;
    }
}
