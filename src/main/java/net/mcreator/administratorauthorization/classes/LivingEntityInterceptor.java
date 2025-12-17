package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.configuration.AAInterceptorConfiguration;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public abstract class LivingEntityInterceptor extends LivingEntity {
    protected LivingEntityInterceptor(EntityType<? extends LivingEntityInterceptor> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public float getHealth() {
        if (!AAInterceptorConfiguration.GET_HEALTH.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            return super.getHealth();
        }
        return this.entityData.get(
                ((LivingEntityAccess) this).administrator_authorization$getAccessorHealth()
        );
    }

    @Override
    public boolean isDeadOrDying() {
        if (!AAInterceptorConfiguration.IS_DEAD.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            return super.isDeadOrDying();
        }
        return false;
    }

    @Override
    public boolean isAlive() {
        if (!AAInterceptorConfiguration.IS_ALIVE.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            return super.isAlive();
        }
        return true;
    }

    @Override
    public void setHealth(float pHealth) {
        if (!AAInterceptorConfiguration.SET_HEALTH.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            super.setHealth(pHealth);
        }
    }

    @Override
    public void die(@NotNull DamageSource pDamageSource) {
        if (!AAInterceptorConfiguration.DIE.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            super.die(pDamageSource);
        }
    }

    @Override
    protected boolean isImmobile() {
        if (!AAInterceptorConfiguration.IS_IMMOBILE.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            return super.isImmobile();
        }
        return false;
    }

    @Override
    public void remove(@NotNull RemovalReason pReason) {
        if (!AAInterceptorConfiguration.REMOVE.get() || (this instanceof EntityAccess entityAccess && entityAccess.administrator_authorization$getAuthorization())) {
            super.remove(pReason);
        }
        if (!pReason.equals(RemovalReason.KILLED)) {
            super.remove(pReason);
        }
    }
}
