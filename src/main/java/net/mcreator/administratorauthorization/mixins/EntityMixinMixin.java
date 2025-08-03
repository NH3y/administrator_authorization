package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.*;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@Mixin(value = Entity.class, priority = Integer.MIN_VALUE)
public abstract class EntityMixinMixin extends CapabilityProvider implements EntityAccess {
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
    @Unique
    private boolean administrator_authorization$Authorized = false;
    @Unique
    private boolean administrator_authorization$authorizeSwitch = true;

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
            //ObjectCollection<SynchedEntityData.DataItem<?>> dataItems = ((EntityDataAccess) this.entityData).administrator_authorization$getAllItems();
            Set<Map.Entry<Attribute, AttributeInstance>> entries = ((AttributeAccess) player.getAttributes()).administrator_authorization$getAllAttributes().entrySet();
            for (Map.Entry<Attribute, AttributeInstance> instanceEntry : entries) {
                if (instanceEntry.getKey().getDescriptionId().toLowerCase().replace("_", "").replace(".", "").contains("resist")
                && instanceEntry.getKey() instanceof RangedAttribute rangedAttribute) {
                    ((AttributeAccess) player.getAttributes()).administrator_authorization$replaceValue(
                            instanceEntry.getKey(),
                            rangedAttribute.getMaxValue()
                    );
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
            this.setRemoved(reason.next());
        }
        this.invalidateCaps();
        this.onRemovedFromWorld();
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
}
