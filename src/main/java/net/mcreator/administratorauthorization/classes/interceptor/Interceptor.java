package net.mcreator.administratorauthorization.classes.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Callable;

@SuppressWarnings("unused")
public interface Interceptor {
    Logger logger = LoggerFactory.getLogger(Interceptor.class);

    float getHealth(
            LivingEntity self,
            Callable<Float> original
    ) throws Exception;
    float m_21223_(
            LivingEntity self,
            Callable<Float> original
    ) throws Exception;

    void setHealth(
            float pHealth,
            LivingEntity self,
            Callable<Void> original
    ) throws Exception;
    void m_21153_(
            float value,
            LivingEntity self,
            Callable<Void> original
    ) throws Exception;

    boolean hurt(
            DamageSource pSource,
            float pAmount,
            LivingEntity self,
            Callable<Boolean> original
    ) throws Exception;
    boolean m_6469_(
            DamageSource pSource,
            float pAmount,
            LivingEntity self,
            Callable<Boolean> original
    ) throws Exception;

    void die(
            DamageSource damageSource,
            LivingEntity self,
            Callable<Void> original
    ) throws Exception;
    void m_6667_(
            DamageSource damageSource,
            LivingEntity self,
            Callable<Void> original
    ) throws Exception;

    boolean isDeadOrDying(
            LivingEntity self,
            Callable<Boolean> original
    );
    boolean m_21224_(
            LivingEntity self,
            Callable<Boolean> original
    );
}
