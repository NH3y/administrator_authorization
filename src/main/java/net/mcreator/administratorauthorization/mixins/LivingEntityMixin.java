package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.classes.Vault;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.Stats;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(value = LivingEntity.class, priority = Integer.MIN_VALUE)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {

    public LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Shadow
    protected abstract void dropAllDeathLoot(DamageSource p_21192_);

    @Shadow
    @Nullable
    public abstract LivingEntity getKillCredit();

    @Shadow
    @Final
    private AttributeMap attributes;

    @Shadow
    protected boolean dead;

    @Shadow
    public abstract void setHealth(float pHealth);

    @Shadow
    @Final
    private static EntityDataAccessor<Float> DATA_HEALTH_ID;

    @Shadow
    public abstract float getMaxHealth();

    @Shadow
    @Final
    public static int HAND_SLOTS;
    @Shadow
    @Final
    public static int DEATH_DURATION;

    @Shadow
    public abstract float getHealth();

    @Shadow
    public abstract double getAttributeBaseValue(Attribute pAttribute);

    @Shadow
    public int deathTime;

    @Shadow
    public abstract boolean isSleeping();

    @Shadow
    protected int fallFlyTicks;

    @Shadow
    public abstract boolean isFallFlying();

    @Shadow
    protected float animStep;
    @Shadow
    public float yHeadRot;
    @Shadow
    public float yHeadRotO;
    @Shadow
    public float yBodyRot;
    @Shadow
    public float yBodyRotO;

    @Shadow
    protected abstract float tickHeadTurn(float pYRot, float pAnimStep);

    @Shadow
    protected float run;
    @Shadow
    public float attackAnim;
    @Shadow
    protected float oRun;

    @Shadow
    public abstract void aiStep();

    @Shadow
    protected abstract boolean checkBedExists();

    @Shadow
    public abstract CombatTracker getCombatTracker();

    @Shadow
    protected abstract void detectEquipmentUpdates();

    @Shadow
    public abstract void setStingerCount(int pStingerCount);

    @Shadow
    public int removeStingerTime;

    @Shadow
    public abstract int getStingerCount();

    @Shadow
    public abstract void setArrowCount(int pCount);

    @Shadow
    public int removeArrowTime;

    @Shadow
    public abstract int getArrowCount();

    @Shadow
    protected abstract void updateSwimAmount();

    @Shadow
    protected abstract void updatingUsingItem();

    @Shadow
    protected int deathScore;

    @Shadow
    public abstract void stopSleeping();

    @Shadow
    protected int noActionTime;

    @Shadow
    public abstract boolean isDamageSourceBlocked(DamageSource pDamageSource);

    @Shadow
    protected abstract void hurtCurrentlyUsedShield(float pDamageAmount);

    @Shadow
    protected abstract void blockUsingShield(LivingEntity pAttacker);

    @Shadow
    @Final
    public WalkAnimationState walkAnimation;
    @Shadow
    protected float lastHurt;

    @Shadow
    protected abstract void actuallyHurt(DamageSource pDamageSource, float pDamageAmount);

    @Shadow
    public abstract boolean hurt(@NotNull DamageSource pSource, float pAmount);

    @Shadow
    public abstract void setLastHurtByMob(@org.jetbrains.annotations.Nullable LivingEntity pLivingEntity);

    @Shadow
    protected int lastHurtByPlayerTime;
    @Shadow
    @Nullable
    protected Player lastHurtByPlayer;

    @Shadow
    public abstract void knockback(double pStrength, double pX, double pZ);

    @Shadow
    public abstract void indicateDamage(double pXDistance, double pZDistance);

    @Shadow
    public abstract boolean isDeadOrDying();

    @Shadow
    @Nullable
    protected abstract SoundEvent getDeathSound();

    @Shadow
    protected abstract float getSoundVolume();

    @Shadow
    public abstract float getVoicePitch();

    @Shadow
    protected abstract void playHurtSound(DamageSource pSource);

    @Shadow
    @Nullable
    private DamageSource lastDamageSource;
    @Shadow
    private long lastDamageStamp;

    @Shadow
    public abstract float getAbsorptionAmount();

    @Shadow
    public abstract void setAbsorptionAmount(float pAbsorptionAmount);

    @Unique
    private boolean administrator_authorization$NoAI = false;

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    public void setHealth_head(float p_21154_, CallbackInfo ci) {
        Vault.EntityCallContext.push(this);
        if ((Object) this instanceof Player) {
            if (((EntityAccess) this).administrator_authorization$getAuthorization() && !(p_21154_ >= this.administrator_authorization$getFixedMaxHealth())) {
                ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(DATA_HEALTH_ID, this.administrator_authorization$getFixedMaxHealth());
                ci.cancel();
                AdministratorAuthorizationMod.LOGGER.info("Mixin : setHealth");
            }
        }
        if ((Object) this instanceof LivingEntity living) {
            if (p_21154_ >= getHealth() && HealthDataOperant.getHealthLimit(living) < p_21154_ && HealthDataOperant.getHealthLock(living) ) {
                System.out.println("Health Limit : " + HealthDataOperant.getHealthLimit(living) + " Excess!");
                ci.cancel();
            }
        }
    }

    @Inject(method = "setHealth", at = @At("RETURN"))
    public void setHealth_return(float p_21154_, CallbackInfo ci) {
        Vault.EntityCallContext.clear();
    }

    @Inject(method = "getHealth", at = @At("HEAD"), cancellable = true)
    public void getHealth0(CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(this.administrator_authorization$getFixedMaxHealth());
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource p_21014_, CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
            this.dead = false;
            AdministratorAuthorizationMod.LOGGER.info("Mixin : Die");
        }
    }

    @Inject(method = "aiStep", at = @At("HEAD"), cancellable = true)
    public void blockAI(CallbackInfo ci) {
        if (this.administrator_authorization$NoAI) ci.cancel();
    }

    @Inject(method = "isDeadOrDying", at = @At("RETURN"), cancellable = true)
    public void isDeadOrDying(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            if (cir.getReturnValue()) {
                ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                        this.administrator_authorization$getAccessorHealth(),
                        this.administrator_authorization$getFixedMaxHealth()
                );
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "tickDeath", at = @At("HEAD"), cancellable = true)
    public void tickDeath(CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
            this.deathTime = 0;
            this.dead = false;
        }
    }

    @Inject(method = "getMaxHealth", at = @At("HEAD"), cancellable = true)
    public void getMaxHealth(CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            float health = (float) Math.min(Math.abs(this.getAttributeBaseValue(Attributes.MAX_HEALTH)), Float.MAX_VALUE);
            cir.setReturnValue(Math.max(health, 20));
        }
    }

    @Inject(method = "handleDamageEvent", at = @At("HEAD"), cancellable = true)
    public void handleDamageEvent(DamageSource pDamageSource, CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleEntityEvent", at = @At("HEAD"), cancellable = true)
    public void handleEntityEvent(byte pId, CallbackInfo ci) {
        if (pId == 3 && ((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "isAlive", at = @At("RETURN"), cancellable = true)
    public void isAlive(CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void tickHead(CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            if (((EntityAccess) this).administrator_authorization$isEmergency()) {
                this.administrator_authorization$protectedTick();
                ci.cancel();
            }
            this.dead = false;
            this.deathTime = 0;
            ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                    this.administrator_authorization$getAccessorHealth(),
                    this.administrator_authorization$getFixedMaxHealth()
            );
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            this.dead = false;
            this.deathTime = 0;
            ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                    this.administrator_authorization$getAccessorHealth(),
                    this.administrator_authorization$getFixedMaxHealth()
            );
            if (this.getPose() == Pose.DYING) {
                this.setPose(Pose.STANDING);
            }
        }
    }

    @Inject(method = "isDamageSourceBlocked", at = @At("HEAD"), cancellable = true)
    public void isDamageSourceBlocked(DamageSource pDamageSource, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    public void knockback(double pStrength, double pX, double pZ, CallbackInfo ci) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "getDamageAfterArmorAbsorb", at = @At("HEAD"), cancellable = true)
    public void getDamageAfterArmorAbsorb(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "getDamageAfterMagicAbsorb", at = @At("HEAD"), cancellable = true)
    public void getDamageAfterMagicAbsorb(DamageSource pDamageSource, float pDamageAmount, CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(0.0F);
        }
    }

    @Inject(method = "getWaterSlowDown", at = @At("RETURN"), cancellable = true)
    public void getWaterSlowDown(CallbackInfoReturnable<Float> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(1.0F);
        }
    }

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource pSource, float pAmount, CallbackInfoReturnable<Boolean> cir) {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
            this.dead = false;
        }
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    public void actuallyHurt(DamageSource pDamageSource, float pDamageAmount, CallbackInfo ci) {
        Vault.HurtByContext.push(new Vault.HurtByContext.Context(pDamageSource, pDamageAmount));
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            ci.cancel();
            this.dead = false;
        }
    }

    @Inject(method = "actuallyHurt", at = @At("RETURN"))
    public void actuallyHurt_return(DamageSource pDamageSource, float pDamageAmount, CallbackInfo ci) {
        Vault.HurtByContext.clear();
    }

    @Unique
    private void administrator_authorization$protectedTick() {
        ((EntityAccess) this).administrator_authorization$tickEmergency();
        this.dead = false;
        this.deathTime = 0;
        ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                this.administrator_authorization$getAccessorHealth(),
                this.administrator_authorization$getFixedMaxHealth()
        );

        if (ForgeHooks.onLivingTick((LivingEntity) (Object) this)) return;
        super.tick();
        this.updatingUsingItem();
        this.updateSwimAmount();
        if (!this.level().isClientSide) {
            int i = this.getArrowCount();
            if (i > 0) {
                if (this.removeArrowTime <= 0) {
                    this.removeArrowTime = 20 * (30 - i);
                }

                --this.removeArrowTime;
                if (this.removeArrowTime <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            int j = this.getStingerCount();
            if (j > 0) {
                if (this.removeStingerTime <= 0) {
                    this.removeStingerTime = 20 * (30 - j);
                }

                --this.removeStingerTime;
                if (this.removeStingerTime <= 0) {
                    this.setStingerCount(j - 1);
                }
            }

            this.detectEquipmentUpdates();
            if (this.tickCount % 20 == 0) {
                this.getCombatTracker().recheckStatus();
            }

            if (this.isSleeping() && !this.checkBedExists()) {
                this.stopRiding();
            }
        }

        if (!this.isRemoved()) {
            this.aiStep();
        }

        double d1 = this.getX() - this.xo;
        double d0 = this.getZ() - this.zo;
        float f = (float) (d1 * d1 + d0 * d0);
        float f1 = this.yBodyRot;
        float f2 = 0.0F;
        this.oRun = this.run;
        float f3 = 0.0F;
        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt(f) * 3.0F;
            float f4 = (float) Mth.atan2(d0, d1) * (180F / (float) Math.PI) - 90.0F;
            float f5 = Mth.abs(Mth.wrapDegrees(this.getYRot()) - f4);
            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.attackAnim > 0.0F) {
            f1 = this.getYRot();
        }

        if (!this.onGround()) {
            f3 = 0.0F;
        }

        this.run += (f3 - this.run) * 0.3F;
        this.level().getProfiler().push("headTurn");
        f2 = this.tickHeadTurn(f1, f2);
        this.level().getProfiler().pop();
        this.level().getProfiler().push("rangeChecks");

        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }

        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO < -180.0F) {
            this.yBodyRotO -= 360.0F;
        }

        while (this.yBodyRot - this.yBodyRotO >= 180.0F) {
            this.yBodyRot += 360.0F;
        }

        while (this.getXRot() - this.xRotO < -180.0F) {
            this.xRotO -= 360.0F;
        }

        while (this.getXRot() - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO < -180.0F) {
            this.yHeadRotO -= 360.0F;
        }

        while (this.yHeadRot - this.yHeadRotO >= 180.0F) {
            this.yHeadRotO += 360.0F;
        }

        this.level().getProfiler().pop();
        this.animStep += f2;
        if (this.isFallFlying()) {
            ++this.fallFlyTicks;
        } else {
            this.fallFlyTicks = 0;
        }

        if (this.isSleeping()) {
            this.setXRot(0.0F);
        }

        this.dead = false;
        this.deathTime = 0;
        ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                this.administrator_authorization$getAccessorHealth(),
                this.administrator_authorization$getFixedMaxHealth()
        );
        if (this.getPose() == Pose.DYING) {
            this.setPose(Pose.STANDING);
        }
    }

    @Override
    public void administrator_authorization$accessDropLoot(LevelAccessor world) {
        this.dropAllDeathLoot(new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolderOrThrow(ResourceKey.create(
                        Registries.DAMAGE_TYPE,
                        new ResourceLocation("administrator_authorization:chaotic_void"))), this.getKillCredit())
        );
    }

    @Override
    public void Administrator_authorization$setNoAI(boolean Disable) {
        this.administrator_authorization$NoAI = Disable;
    }

    @Override
    public void administrator_authorization$setAttributes(Attribute attribute, double value) {
        AttributeInstance instance = this.attributes.getInstance(attribute);
        if (instance == null) {
            return;
        }

        try {
            ((AttributeAccess) this.attributes).administrator_authorization$replaceValue(attribute, value);
        } catch (RuntimeException ignored) {

        }

    }

    @Override
    public float administrator_authorization$getFixedMaxHealth() {
        float health = Math.max(0 + Math.abs(this.getMaxHealth()), Math.max(DEATH_DURATION, 20));
        return health < Float.MAX_VALUE ? health : 20;
    }

    @Override
    public void administrator_authorization$setHealth(float value) {
        ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(DATA_HEALTH_ID, value);
    }

    @Override
    public EntityDataAccessor<Float> administrator_authorization$getAccessorHealth() {
        return DATA_HEALTH_ID;
    }

    @Override
    public void administrator_authorization$die(DamageSource pDamageSource) {
        if (net.minecraftforge.common.ForgeHooks.onLivingDeath((LivingEntity)(Object) this, pDamageSource)) return;
        if (!this.isRemoved() && !this.dead) {
            Entity entity = pDamageSource.getEntity();
            LivingEntity livingentity = this.getKillCredit();
            if (this.deathScore >= 0 && livingentity != null) {
                livingentity.awardKillScore(this, this.deathScore, pDamageSource);
            }

            if (this.isSleeping()) {
                this.stopSleeping();
            }

            this.dead = true;
            this.getCombatTracker().recheckStatus();
            Level level = this.level();
            if (level instanceof ServerLevel serverlevel) {
                if (entity != null) {
                    entity.killedEntity(serverlevel, (LivingEntity) (Object) this);
                }
                this.gameEvent(GameEvent.ENTITY_DIE);
                this.dropAllDeathLoot(pDamageSource);

                this.level().broadcastEntityEvent(this, (byte)3);
            }

            this.setPose(Pose.DYING);
        }
    }

    @Override
    public void administrator_authorization$hurt(DamageSource pSource, float pAmount) {
        if (!this.level().isClientSide) {
            if (this.isSleeping() && !this.level().isClientSide) {
                this.stopSleeping();
            }

            this.noActionTime = 0;
            boolean flag = false;
            float f1 = 0.0F;

            this.walkAnimation.setSpeed(1.5F);
            this.lastHurt = pAmount;
            this.administrator_authorization$actuallyHurt(pSource, pAmount);

            Entity entity1 = pSource.getEntity();
            if (entity1 != null) {
                if (entity1 instanceof LivingEntity livingentity1) {
                    if (!pSource.is(DamageTypeTags.NO_ANGER)) {
                        this.setLastHurtByMob(livingentity1);
                    }
                }

                if (entity1 instanceof Player player1) {
                    this.lastHurtByPlayerTime = 100;
                    this.lastHurtByPlayer = player1;
                } else if (entity1 instanceof net.minecraft.world.entity.TamableAnimal tamableEntity) {
                    if (tamableEntity.isTame()) {
                        this.lastHurtByPlayerTime = 100;
                        LivingEntity livingentity2 = tamableEntity.getOwner();
                        if (livingentity2 instanceof Player) {
                            this.lastHurtByPlayer = (Player)livingentity2;
                        } else {
                            this.lastHurtByPlayer = null;
                        }
                    }
                }
            }

            this.level().broadcastDamageEvent(this, pSource);

            if (!pSource.is(DamageTypeTags.NO_IMPACT)) {
                this.markHurt();
            }

            if (entity1 != null && !pSource.is(DamageTypeTags.IS_EXPLOSION)) {
                double d0 = entity1.getX() - this.getX();

                double d1;
                for(d1 = entity1.getZ() - this.getZ(); d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                    d0 = (Math.random() - Math.random()) * 0.01D;
                }

                this.knockback(0.4F, d0, d1);
                if (!flag) {
                    this.indicateDamage(d0, d1);
                }
            }

            if (this.isDeadOrDying()) {
                SoundEvent soundevent = this.getDeathSound();
                if (soundevent != null) {
                    this.playSound(soundevent, this.getSoundVolume(), this.getVoicePitch());
                }

                this.administrator_authorization$die(pSource);
            } else {
                this.playHurtSound(pSource);
            }

            this.lastDamageSource = pSource;
            this.lastDamageStamp = this.level().getGameTime();

            if ((Object)this instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.ENTITY_HURT_PLAYER.trigger(serverPlayer, pSource, pAmount, pAmount, flag);
                if (f1 > 0.0F && f1 < 3.4028235E37F) {
                    serverPlayer.awardStat(Stats.CUSTOM.get(Stats.DAMAGE_BLOCKED_BY_SHIELD), Math.round(f1 * 10.0F));
                }
            }

            if (entity1 instanceof ServerPlayer) {
                CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((ServerPlayer)entity1, this, pSource, pAmount, pAmount, flag);
            }

        }
    }

    @Override
    public void administrator_authorization$actuallyHurt(DamageSource pDamageSource, float pDamageAmount) {
        pDamageAmount = ForgeHooks.onLivingHurt((LivingEntity) (Object) this, pDamageSource, pDamageAmount);
        if (pDamageAmount <= 0) return;
        float f1 = Math.max(pDamageAmount - this.getAbsorptionAmount(), 0.0F);
        this.setAbsorptionAmount(0);
        float f = pDamageAmount;
        Entity entity = pDamageSource.getEntity();
        if (entity instanceof ServerPlayer serverplayer) {
            serverplayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(f * 10.0F));
        }

        f1 = ForgeHooks.onLivingDamage((LivingEntity) (Object) this, pDamageSource, f1);
        if (f1 != 0.0F) {
            this.getCombatTracker().recordDamage(pDamageSource, f1);
            this.administrator_authorization$setHealth(this.getHealth() - f1);
            this.setAbsorptionAmount(0);
            this.gameEvent(GameEvent.ENTITY_DAMAGE);
        }
    }

    static {
        Vault.healthId.setData(DATA_HEALTH_ID.getId());
    }
}
