package net.mcreator.administratorauthorization.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccessorsAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
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

@Mixin(value = SynchedEntityData.class, priority = Integer.MIN_VALUE)
public abstract class SynchedEntityDataMixin implements EntityDataAccess {
    @Shadow @Final private Entity entity;

    @Shadow
    public static <T> EntityDataAccessor<T> defineId(Class<? extends Entity> pClazz, EntityDataSerializer<T> pSerializer) {
        return null;
    }

    @Shadow
    private <T> SynchedEntityData.DataItem<T> getItem(EntityDataAccessor<T> pKey) {
        return null;
    }

    @Shadow public abstract <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce);

    @Shadow private boolean isDirty;

    @Shadow @Final private ReadWriteLock lock;

    @Shadow @Final private Int2ObjectMap<SynchedEntityData.DataItem<?>> itemsById;

    @Shadow public abstract <T> T get(EntityDataAccessor<T> pKey);

    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private final Set<Integer> administrator_authorization$bannedId = new HashSet<>(4);

    @Inject(method = "set(Lnet/minecraft/network/syncher/EntityDataAccessor;Ljava/lang/Object;Z)V", at = @At("HEAD"), cancellable = true)
    public <T> void set(EntityDataAccessor<T> pKey, T pValue, boolean pForce, CallbackInfo ci) {
        if(((EntityAccess) this.entity).administrator_authorization$getAuthorization()){
            if(this.administrator_authorization$bannedId.contains(pKey.getId())){
                ci.cancel();
                AdministratorAuthorizationMod.LOGGER.info("Mixin : setEntityData");
            } else if ((AAAuthorizationConfiguration.BAN_SUSPECT.get() || ((EntityAccess) this.entity).administrator_authorization$isEmergency()) && this.get(pKey).getClass().isAssignableFrom(Float.class)) {
                AdministratorAuthorizationMod.LOGGER.info("Mixin : Ban Suspicious Data {} ( value: {} / id: {} )"
                        ,((EntityDataAccessorsAccess) pKey).administrator_authorization$catchName()
                        ,this.itemsById.get(pKey.getId()).getValue()
                        ,pKey.getId()
                );
                ci.cancel();
            } else {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (int i = 1; i < stackTrace.length; i++) {
                    if(stackTrace[i].getMethodName().toLowerCase().contains("hurt") || stackTrace[i].getClassName().toLowerCase().contains("hurt")){
                        LOGGER.warn("Find method {} tweak {} to {}",
                                stackTrace[i].getMethodName(),
                                ((EntityDataAccessorsAccess) pKey).administrator_authorization$catchName(),
                                pValue
                                );
                        ci.cancel();
                        break;
                    }
                }
            }
        }
    }

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    public <T> void get(EntityDataAccessor<T> pKey, CallbackInfoReturnable<Float> cir){
        if(((EntityAccess) this.entity).administrator_authorization$getAuthorization()){
            if(this.entity instanceof LivingEntityAccess access && access.administrator_authorization$getAccessorHealth().equals(pKey)){
                cir.setReturnValue(
                        access.administrator_authorization$getFixedMaxHealth()
                );
            }
        }
    }

    @Override
    public <T> void administrator_authorization$forceSet(EntityDataAccessor<T> pKey, T pValue){
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
            dataitem = (SynchedEntityData.DataItem<T>)this.itemsById.get(pKey.getId());
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
    public ObjectCollection<SynchedEntityData.DataItem<?>> administrator_authorization$getAllItems(){
       return this.itemsById.values();
    }

    @Override
    public Set<Integer> Administrator_authorization$getBannedId() {
        return administrator_authorization$bannedId;
    }
}
