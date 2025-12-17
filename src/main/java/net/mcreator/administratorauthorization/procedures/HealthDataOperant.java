package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.minecraft.world.entity.LivingEntity;

public class HealthDataOperant {
    public static float getHealthLimit(LivingEntity living) {
        return living.getData(AttachmentRegistry.HEALTH_DATA.get()).getHealthLimit();
    }

    public static void updateHealthLimit(LivingEntity living, float newFloat) {
        living.getData(AttachmentRegistry.HEALTH_DATA.get()).setHealthLimit(newFloat);
    }

    public static boolean getHealthLock(LivingEntity living) {
        return living.getData(AttachmentRegistry.HEALTH_DATA.get()).isHealthLock();
    }

    public static void updateHealthLock(LivingEntity living, boolean newBoolean) {
        living.getData(AttachmentRegistry.HEALTH_DATA.get()).setHealthLock(newBoolean);
    }
}
