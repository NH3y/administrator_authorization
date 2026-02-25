package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.DataItemAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityDataAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.world.entity.LivingEntity;

@SuppressWarnings("unchecked")
public class HealthDataOperant {
    public static float getHealthLimit(LivingEntity living) {
        DataItemAccess<Float> dataItemAccess = (DataItemAccess<Float>) ((EntityDataAccess) living.getEntityData())
                .administrator_authorization$publicGetItem(((LivingEntityAccess) living).administrator_authorization$getAccessorHealth());
        if (dataItemAccess.administrator_authorization$getLock() == 4) {
            return dataItemAccess.administrator_authorization$getLockValue();
        }
        return Float.MAX_VALUE;
    }

    public static void updateHealthLimit(LivingEntity living, float newFloat) {
        DataItemAccess<Float> dataItemAccess = (DataItemAccess<Float>) ((EntityDataAccess) living.getEntityData())
                .administrator_authorization$publicGetItem(((LivingEntityAccess) living).administrator_authorization$getAccessorHealth());
        dataItemAccess.administrator_authorization$setLockValue(newFloat);
    }

    public static boolean getHealthLock(LivingEntity living) {
        DataItemAccess<Float> dataItemAccess = (DataItemAccess<Float>) ((EntityDataAccess) living.getEntityData())
                .administrator_authorization$publicGetItem(((LivingEntityAccess) living).administrator_authorization$getAccessorHealth());
        return dataItemAccess.administrator_authorization$getLock() == 4;
    }

    public static void updateHealthLock(LivingEntity living, boolean newBoolean) {
        DataItemAccess<Float> dataItemAccess = (DataItemAccess<Float>) ((EntityDataAccess) living.getEntityData())
                .administrator_authorization$publicGetItem(((LivingEntityAccess) living).administrator_authorization$getAccessorHealth());
        dataItemAccess.administrator_authorization$toLock(newBoolean ? 4 : 0);
    }
}
