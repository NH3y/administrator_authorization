package net.mcreator.administratorauthorization.capabilities;

import net.mcreator.administratorauthorization.Interfaces.IHealthData;
import net.minecraft.nbt.CompoundTag;

public class HealthData implements IHealthData {
    private float HealthLimit = 0;
    private boolean HealthLock = false;

    @Override
    public float getHealthLimit(){
        return HealthLimit;
    }

    @Override
    public void setHealthLimit(float limit){
        this.HealthLimit = limit;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("healthLimit", HealthLimit);
        tag.putBoolean("healthLock", HealthLock);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag){
        HealthLimit = tag.getFloat("healthLimit");
        HealthLock = tag.getBoolean("healthLock");
    }

    @Override
    public void setHealthLock(boolean lock){
        this.HealthLock = lock;
    }

    @Override
    public boolean isHealthLock() {
        return HealthLock;
    }
}
