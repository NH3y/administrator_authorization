package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.CombatTracker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
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
    public abstract double getAttributeBaseValue(Holder<Attribute> attribute);

    @Shadow
    protected abstract void dropAllDeathLoot(ServerLevel p_level, DamageSource damageSource);

    @Unique
    private boolean administrator_authorization$NoAI = false;

    @Inject(method = "setHealth", at = @At("HEAD"), cancellable = true)
    public void administrator_authorization$setHealth(float p_21154_, CallbackInfo ci) {
        if ((Object) this instanceof Player) {
            if (((EntityAccess) this).administrator_authorization$getAuthorization() && !(p_21154_ >= this.administrator_authorization$getFixedMaxHealth())) {
                ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(DATA_HEALTH_ID, this.administrator_authorization$getFixedMaxHealth());
                ci.cancel();
                AdministratorAuthorizationMod.LOGGER.info("Mixin : setHealth");
            }
        }
        if ((Object) this instanceof LivingEntity living) {
            if (HealthDataOperant.getHealthLimit(living) < p_21154_ && HealthDataOperant.getHealthLock(living)) {
                System.out.println("Health Limit : " + HealthDataOperant.getHealthLimit(living) + " Excess!");
                ci.cancel();
            }
        }
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

    @Unique
    private void administrator_authorization$protectedTick() {
        ((EntityAccess) this).administrator_authorization$tickEmergency();
        this.dead = false;
        this.deathTime = 0;
        ((EntityDataAccess) this.entityData).administrator_authorization$forceSet(
                this.administrator_authorization$getAccessorHealth(),
                this.administrator_authorization$getFixedMaxHealth()
        );

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
        if (this.level() instanceof ServerLevel serverLevel) {
            this.dropAllDeathLoot(
                    serverLevel,
                    new DamageSource(world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(ResourceKey.create(
                            Registries.DAMAGE_TYPE,
                            ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "chaotic_void"))), this.getKillCredit())
            );
        }
    }

    @Override
    public void Administrator_authorization$setNoAI(boolean Disable) {
        this.administrator_authorization$NoAI = Disable;
    }

    @Override
    public void administrator_authorization$setAttributes(Holder<Attribute> attribute, double value) {
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
}
