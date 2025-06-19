package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow protected abstract void dropAllDeathLoot(DamageSource p_21192_);

    @Shadow @Nullable public abstract LivingEntity getKillCredit();

    @Shadow @Final private AttributeMap attributes;

    @Shadow protected boolean dead;

    @Shadow public abstract void setHealth(float pHealth);

    @Shadow @Final private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow public abstract float getMaxHealth();

    @Shadow @Final public static int HAND_SLOTS;
    @Shadow @Final public static int DEATH_DURATION;
    @Unique
    private float administrator_authorization$healthCap = Float.MAX_VALUE;

    @Unique
    private boolean administrator_authorization$NoAI = false;

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    public void administrator_authorization$setHealth(float p_21154_, CallbackInfo ci){
        if ((Object)this instanceof Player) {
            if(((EntityAccess) this).administrator_authorization$getAuthorization() && !(p_21154_ >= 20)) {
                this.entityData.set(DATA_HEALTH_ID,this.administrator_authorization$getFixedMaxHealth());
                ci.cancel();
            }
        }
        if(this.administrator_authorization$healthCap < p_21154_){
            ci.cancel();
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void administrator_authorization$die(DamageSource p_21014_, CallbackInfo ci){
        if(((EntityAccess) this).administrator_authorization$getAuthorization()){
            ci.cancel();
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    public void blockAI(CallbackInfo ci){
        if(this.administrator_authorization$NoAI) ci.cancel();
    }

    @Override
    public void administrator_authorization$accessDropLoot(LevelAccessor world){
        this.dropAllDeathLoot(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(
                        Registries.DAMAGE_TYPE,
                        new ResourceLocation("administrator_authorization:chaotic	_void"))),this.getKillCredit())
        );
    }

    @Override
    public float administrator_authorization$getHealthCap() {
        return this.administrator_authorization$healthCap;
    }

    @Override
    public void administrator_authorization$setHealthCap(float healthCap) {
        this.administrator_authorization$healthCap = healthCap;
    }

    @Override
    public void Administrator_authorization$setNoAI(boolean Disable) {
        this.administrator_authorization$NoAI = Disable;
    }

    @Override
    public void administrator_authorization$setAttributes(Attribute attribute, double value){
        AttributeInstance instance = this.attributes.getInstance(attribute);
        if (instance == null) {
            return;
        }
        ((AttributeAccess) this.attributes).administrator_authorization$replaceValue(attribute, this.administrator_authorization$getFixedMaxHealth());
    }

    @Override
    public float administrator_authorization$getFixedMaxHealth(){
        float health = Math.max(0 + Math.abs(this.getMaxHealth()), Math.max(DEATH_DURATION, 20));
        return health < Float.MAX_VALUE ? health : 20;
    }
}
