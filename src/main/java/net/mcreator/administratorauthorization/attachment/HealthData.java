package net.mcreator.administratorauthorization.attachment;

import net.mcreator.administratorauthorization.Interfaces.IHealthData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class HealthData implements IHealthData, INBTSerializable<CompoundTag> {
    private float HealthLimit = 0;
    private boolean HealthLock = false;

    @Override
    public float getHealthLimit() {
        return HealthLimit;
    }

    @Override
    public void setHealthLimit(float limit) {
        this.HealthLimit = limit;
    }

    @Override
    public void setHealthLock(boolean lock) {
        this.HealthLock = lock;
    }

    @Override
    public boolean isHealthLock() {
        return HealthLock;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("healthLimit", HealthLimit);
        tag.putBoolean("healthLock", HealthLock);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag compoundTag) {
        HealthLimit = compoundTag.getFloat("healthLimit");
        HealthLock = compoundTag.getBoolean("healthLock");
    }
}
