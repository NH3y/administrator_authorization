package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.*;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.*;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.*;

@SuppressWarnings("UnstableApiUsage")
@Mixin(value = Entity.class, priority = Integer.MIN_VALUE)
public abstract class EntityMixinMixin extends CapabilityProvider implements EntityAccess {
    @SuppressWarnings("unchecked")
    protected EntityMixinMixin() {
        super(Entity.class);
    }

    @Shadow public abstract void setRemoved(Entity.RemovalReason pRemovalReason);

    @Shadow private Level level;

    @Shadow protected abstract void unsetRemoved();

    @Shadow @Final protected SynchedEntityData entityData;

    @Shadow(remap = false)
    public abstract void revive();

    @Shadow(remap = false)
    public abstract void onRemovedFromWorld();

    @Shadow @Nullable private Entity.RemovalReason removalReason;

    @Shadow @Final private Set<String> tags;
    @Shadow
    private EntityInLevelCallback levelCallback;

    @Shadow
    public abstract void stopRiding();

    @Shadow
    public abstract List<Entity> getPassengers();

    @Shadow
    public abstract CompoundTag saveWithoutId(CompoundTag pCompound);

    @Shadow
    public abstract Level level();

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    @Final
    private static Logger LOGGER;
    @Unique
    private final long CONSTANT_CODE = 66571993088L;
    @Unique
    private boolean administrator_authorization$Authorized = false;
    @Unique
    private boolean administrator_authorization$authorizeSwitch = true;
    @Unique
    private boolean administrator_authorization$forgotten = false;
    @Unique
    private boolean administrator_authorization$rejectSave = false;
    @Unique
    private long administrator_authorization$identityCode = 0;
    @Unique
    private boolean administrator_authorization$emergency = false;
    @Unique
    private int administrator_authorization$emergencyTime = 0;

    @Inject(method = "isOnFire", at = @At("HEAD"), cancellable = true)
    public void isOnFire(CallbackInfoReturnable<Boolean> cir){
        if (this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "fireImmune", at = @At("HEAD"), cancellable = true)
    public void fireImmune(CallbackInfoReturnable<Boolean> cir){
        if (this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "isFreezing", at = @At("HEAD"), cancellable = true)
    public void isFreezing(CallbackInfoReturnable<Boolean> cir){
        if (this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canFreeze", at = @At("HEAD"), cancellable = true)
    public void canFreeze(CallbackInfoReturnable<Boolean> cir){
        if (this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(false);
        }
    }

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
        if((Object)this instanceof Player player){
            this.administrator_authorization$Authorized = true;

            if (AAAuthorizationConfiguration.LOCK_HEALTH.get()) {
                ((DataItemAccess) ((EntityDataAccess) this.entityData).administrator_authorization$publicGetItem(((LivingEntityAccess) player).administrator_authorization$getAccessorHealth())).administrator_authorization$setProtected(true);
            }
            ((EntityDataAccess) this.entityData).Administrator_authorization$getBannedId().add(
                    ((LivingEntityAccess) player).administrator_authorization$getAccessorHealth().getId()
            );
            //ObjectCollection<SynchedEntityData.DataItem<?>> dataItems = ((EntityDataAccess) this.entityData).administrator_authorization$getAllItems();
            Set<Map.Entry<Attribute, AttributeInstance>> entries = ((AttributeAccess) player.getAttributes()).administrator_authorization$getAllAttributes().entrySet();
            for (Map.Entry<Attribute, AttributeInstance> instanceEntry : entries) {
                if (instanceEntry.getKey().getDescriptionId().toLowerCase().replace("_", "").replace(".", "").contains("resist")
                && instanceEntry.getKey() instanceof RangedAttribute rangedAttribute) {
                    try{
                        ((AttributeAccess) player.getAttributes()).administrator_authorization$replaceValue(
                                instanceEntry.getKey(),
                                rangedAttribute.getMaxValue()
                        );
                    }catch (RuntimeException ignore){

                    }
                    System.out.println(instanceEntry.getKey().getDescriptionId());
                }
            }

        }
    }

    @Override
    public void administrator_authorization$setSwitch(boolean select) {
        this.administrator_authorization$authorizeSwitch = select;
    }

    @Override
    public void administrator_authorization$forceRemove(){
        Iterator<Entity.RemovalReason> reason = Arrays.stream(Entity.RemovalReason.values()).iterator();
        while(reason.hasNext()){
            this.administrator_authorization$forceSetRemoved(reason.next());
        }
        this.invalidateCaps();
        this.onRemovedFromWorld();
    }

    @Override
    public void administrator_authorization$forceSetRemoved(Entity.RemovalReason pRemovalReason){
        if (this.removalReason == null) {
            this.removalReason = pRemovalReason;
        }

        this.stopRiding();

        this.getPassengers().forEach(Entity::stopRiding);
        this.levelCallback.onRemove(pRemovalReason);

    }

    @Override
    public EntityInLevelCallback administrator_authorization$getLevelCallback(){
        return this.levelCallback;
    }

    @Inject(method = "isInvulnerableTo", at = @At("RETURN"), cancellable = true)
    public void isInvulnerableTo(DamageSource p_20122_, CallbackInfoReturnable<Boolean> cir){
        if(this.administrator_authorization$getAuthorization() && !cir.getReturnValue()){
            cir.setReturnValue(true);
            //AdministratorAuthorizationMod.LOGGER.info("Mixin : Invulnerable");
        }
    }

    @Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    public void remove(Entity.RemovalReason pReason, CallbackInfo ci){
        if(this.administrator_authorization$getAuthorization() && pReason.equals(Entity.RemovalReason.KILLED)){
            ci.cancel();
            this.revive();
        }
    }

    @Inject(method = "setRemoved", at = @At("HEAD"), cancellable = true)
    public void setRemoved(Entity.RemovalReason pRemovalReason, CallbackInfo ci){
        if(this.administrator_authorization$getAuthorization() && pRemovalReason.equals(Entity.RemovalReason.KILLED)){
            ci.cancel();
            this.revive();
        }
    }

    @Inject(method = "gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V", at = @At("HEAD"), cancellable = true)
    public void gameEvent(GameEvent pEvent, CallbackInfo ci){
        if(this.administrator_authorization$getAuthorization()){
            switch(pEvent.getName()){
                case "entity_damage", "entity_die" -> {
                    ci.cancel();
                    AdministratorAuthorizationMod.LOGGER.info("Mixin : Block GameEvent");
                }
            }
        }
    }

    @Inject(method = "isAlive", at = @At("RETURN"), cancellable = true)
    public void isAlive(CallbackInfoReturnable<Boolean> cir){
        if(this.administrator_authorization$getAuthorization()){
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci){
        if(this.administrator_authorization$getAuthorization()){
            if(this.removalReason == Entity.RemovalReason.KILLED){
                this.removalReason = null;
                this.revive();
            }
        }
    }

    @Inject(method = "saveWithoutId", at = @At("RETURN"), cancellable = true)
    public void saveWithoutId(CompoundTag pCompound, CallbackInfoReturnable<CompoundTag> cir){
        CompoundTag returnValue = cir.getReturnValue();
        returnValue.putLong("LostEntity", this.administrator_authorization$identityCode);
        cir.setReturnValue(returnValue);
    }

    @Inject(method = "hurt", at = @At("HEAD"))
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir){

    }

    @Override
    public boolean Administrator_authorization$isForgotten() {
        return administrator_authorization$forgotten;
    }

    @Override
    public void Administrator_authorization$setForgotten(boolean administrator_authorization$forgotten) {
        this.administrator_authorization$forgotten = administrator_authorization$forgotten;
    }

    @Override
    public boolean administrator_authorization$isRejectSave() {
        return administrator_authorization$rejectSave;
    }

    @Override
    public void administrator_authorization$setRejectSave(boolean administrator_authorization$rejectSave) {
        this.administrator_authorization$identityCode = CONSTANT_CODE;
        if (this.getServer() != null) {
            this.getServer().saveEverything(true, true, true);
        }
        this.administrator_authorization$rejectSave = administrator_authorization$rejectSave;
    }

    @Override
    public boolean administrator_authorization$isEmergency() {
        return administrator_authorization$emergency;
    }

    @Override
    public void administrator_authorization$setEmergency(boolean administrator_authorization$emergency) {
        this.administrator_authorization$emergency = administrator_authorization$emergency;
        if(administrator_authorization$emergency){
            this.administrator_authorization$emergencyTime = 1200;
        }
    }

    @Override
    public void administrator_authorization$tickEmergency(){
        if (this.administrator_authorization$emergencyTime > 0) {
            this.administrator_authorization$emergencyTime--;
        }else {
            this.administrator_authorization$emergency = false;
            this.administrator_authorization$emergencyTime = 0;
        }
    }
}
