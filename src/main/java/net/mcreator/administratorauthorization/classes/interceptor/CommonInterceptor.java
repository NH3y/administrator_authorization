package net.mcreator.administratorauthorization.classes.interceptor;

import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraftforge.common.ForgeHooks;

import java.util.concurrent.Callable;

public class CommonInterceptor implements Interceptor{
    @Override
    public float getHealth(LivingEntity self, Callable<Float> original) throws Exception {
        float v = self.getEntityData()
                .get(((LivingEntityAccess) self).administrator_authorization$getAccessorHealth());
        if (v != original.call()) {
            logger.warn("intercept wrong value {}, should be {}", original.call(), v);
        }
        return v;
    }
    @Override
    public float m_21223_(LivingEntity self, Callable<Float> original) throws Exception {
        return getHealth(self, original);
    }


    @Override
    public void setHealth(float value, LivingEntity self, Callable<Void> original) {
        m_21153_(value, self, original);
    }
    @Override
    public void m_21153_(float value, LivingEntity self, Callable<Void> original) {
        self.getEntityData()
                .set(((LivingEntityAccess) self).administrator_authorization$getAccessorHealth(), value);
    }


    @Override
    public void die(DamageSource damageSource, LivingEntity self, Callable<Void> original) throws Exception {
        m_6667_(damageSource, self, original);
    }
    @Override
    public void m_6667_(DamageSource damageSource, LivingEntity self, Callable<Void> original) throws Exception {
        if (ForgeHooks.onLivingDeath(self, damageSource)) return;
        if (!self.isRemoved() && !((LivingEntityAccess) self).administrator_authorization$deadOperation(false, false)) {
            Entity entity = damageSource.getEntity();

            if (self.isSleeping()) {
                self.stopSleeping();
            }

            if (!self.level().isClientSide && self.hasCustomName()) {
                logger.info("Named entity {} died: {}", self, self.getCombatTracker().getDeathMessage().getString());
            }

            ((LivingEntityAccess) self).administrator_authorization$deadOperation(true, true);
            self.getCombatTracker().recheckStatus();
            Level level = self.level();
            if (level instanceof ServerLevel serverlevel) {
                if (entity == null || entity.killedEntity(serverlevel, self)) {
                    self.gameEvent(GameEvent.ENTITY_DIE);
                }

                self.level().broadcastEntityEvent(self, (byte)3);
            }

            self.setPose(Pose.DYING);
        }
    }

    @Override
    public boolean isDeadOrDying(LivingEntity self, Callable<Boolean> original) {
        return m_21224_(self, original);
    }
    @Override
    public boolean m_21224_(LivingEntity self, Callable<Boolean> original) {
        return self.getHealth() <= 0.0F;
    }


    @Override
    public boolean hurt(DamageSource pSource, float pAmount, LivingEntity self, Callable<Boolean> original) throws Exception {
        return m_6469_(pSource, pAmount, self, original);
    }
    @Override
    public boolean m_6469_(DamageSource pSource, float pAmount, LivingEntity self, Callable<Boolean> original) throws Exception {
        self.setHealth(Math.max(0, self.getHealth() - pAmount));
        return true;
    }
}
