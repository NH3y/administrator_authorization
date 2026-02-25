package net.mcreator.administratorauthorization.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.*;
import net.mcreator.administratorauthorization.classes.Vault;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.security.EntryAnalyzer;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.function.Consumer;

@Mixin(value = SynchedEntityData.class, priority = Integer.MIN_VALUE)
public abstract class SynchedEntityDataMixin implements EntityDataAccess {

    @SuppressWarnings("unused")
    @Unique
    private static Consumer<Vault.HurtByContext.Context> administrator_authorization$AUDIT = context -> {
        Entity entity1 = context.source().getEntity();
        if (entity1 != null && !entity1.getClass().getName().startsWith("net.minecraft.")) {
            System.out.println(entity1.getClass());
        }
    };

    @Shadow
    @Final
    private Entity entity;

    @Shadow
    public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> pClazz, EntityDataSerializer<T> pSerializer) {
        return null;
    }

    @Shadow
    private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> pKey) {
        return null;
    }

    @Shadow
    public abstract <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce);

    @Shadow
    private boolean isDirty;

    @Shadow
    @Final
    private ReadWriteLock lock;

    @Shadow
    @Final
    private Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById;

    @Shadow
    public abstract <T> T get(EntityDataAccessor<T> pKey);

    @Shadow
    @Final
    private static Logger LOGGER;

    @Shadow
    protected abstract <T> void createDataItem(EntityDataAccessor<T> pKey, T pValue);

    @Unique
    private final Set<Integer> administrator_authorization$bannedId = new HashSet<>(4);

    @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("HEAD"), cancellable = true)
    public <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce, CallbackInfo ci) {
        EntryAnalyzer instance = EntryAnalyzer.getInstance();
        instance.mixinMethods();
        instance.recordHurtMethod();
        if (instance.has("net.minecraft.world.entity.player.Player", "m_6256_")) {
            System.out.println("has player attack");
        }
        if (Vault.EntityCallContext.isPresent() && Vault.EntityCallContext.get() instanceof LivingEntity living) {
            if (((EntityAccess) living).administrator_authorization$getAuthorization()) {
                ci.cancel();
            }
            if (AAAuthorizationConfiguration.PENETRATION.get()) {
                administrator_authorization$penetrate(pKey, ci);
            }
        }

        if (((EntityAccess) this.entity).administrator_authorization$getAuthorization()) {
            administrator_authorization$ifAuthorized(pKey, pValue, pForce, ci);
        }
    }

    @Unique
    private <T> void administrator_authorization$penetrate(EntityDataAccessor<T> pKey, CallbackInfo ci) {
        if (Vault.HurtByContext.isPresent(administrator_authorization$AUDIT)) {
            Vault.HurtByContext.Context context = Vault.HurtByContext.get();
            Entity entity1 = context.source().getEntity();
            if (entity1 != null && ((EntityAccess) entity1).administrator_authorization$getAuthorization()) {
                administrator_authorization$ensureSet(pKey, (int) -context.damage(), context.source());
                ci.cancel();
            }
        }
    }

    @Unique
    private <T> void administrator_authorization$ifAuthorized(EntityDataAccessor<T> pKey, T pValue, boolean pForce, CallbackInfo ci) {
        if (pKey.getId() == Vault.healthId.getData())
            ci.cancel();
        if (this.administrator_authorization$bannedId.contains(pKey.getId())) {
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

    @Unique
    private <T> void administrator_authorization$ensureSet(EntityDataAccessor<T> pKey, int alter, DamageSource source) {
        SynchedEntityData.DataItem<T> item = getItem(pKey);
        if (item != null) {
            try {
                SynchedEntityData.DataItem<Float> floatDataItem = (SynchedEntityData.DataItem<Float>) item;
                float health = Math.max(floatDataItem.getValue() + alter, 0);
                floatDataItem.setValue(health);
                if (health == 0 && entity instanceof LivingEntity living) {
                    if (AAAuthorizationConfiguration.CLEAR_DIRECTLY.get()) {
                        ((EntityAccess) entity).administrator_authorization$forceRemove();
                    } else {
                        living.die(source);
                    }
                }
            } catch (ClassCastException ignored) {}
            this.entity.onSyncedDataUpdated(pKey);
            item.setDirty(true);
            this.isDirty = true;
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
        this.lock.readLock().lock();

        SynchedEntityData.DataItem<T> dataitem;
        try {
            dataitem = (SynchedEntityData.DataItem<T>) this.itemsById.get(pKey.getId());
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Getting synched entity data");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Synched entity data");
            crashreportcategory.setDetail("Data ID", pKey);
            throw new ReportedException(crashreport);
        } finally {
            this.lock.readLock().unlock();
        }

        return dataitem;
    }

    @Override
    public ObjectCollection<SynchedEntityData.DataItem<?>> administrator_authorization$getAllItems() {
        return this.itemsById.values();
    }

    @Override
    public Set<Integer> Administrator_authorization$getBannedId() {
        return administrator_authorization$bannedId;
    }
}
