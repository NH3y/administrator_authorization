package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.IHealthData;
import net.mcreator.administratorauthorization.capabilities.HealthDataProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;

public class HealthDataOperant {
    public static float getHealthLimit(LivingEntity living){
        LazyOptional<IHealthData> optional = living.getCapability(HealthDataProvider.HEALTH_DATA);
        return optional
                .map(IHealthData::getHealthLimit)
                .orElse((float) 0);
    }

    public static void updateHealthLimit(LivingEntity living, float newFloat){
        living.getCapability(HealthDataProvider.HEALTH_DATA).ifPresent(data -> data.setHealthLimit(newFloat));
    }

    public static boolean getHealthLock(LivingEntity living){
        LazyOptional<IHealthData> optional = living.getCapability(HealthDataProvider.HEALTH_DATA);
        return optional
                .map(IHealthData::isHealthLock)
                .orElse(false);
    }

    public static void updateHealthLock(LivingEntity living, boolean newBoolean){
        living.getCapability(HealthDataProvider.HEALTH_DATA).ifPresent(data -> data.setHealthLock(newBoolean));
    }
}
