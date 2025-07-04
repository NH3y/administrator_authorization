package net.mcreator.administratorauthorization.Interfaces;

public interface IHealthData {
    float getHealthLimit();

    void setHealthLimit(float limit);

    void setHealthLock(boolean lock);

    boolean isHealthLock();
}
