package net.mcreator.administratorauthorization.capabilities;

import net.mcreator.administratorauthorization.Interfaces.IHealthData;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HealthDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<IHealthData> HEALTH_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final IHealthData healthData = new HealthData();
    private final LazyOptional<IHealthData> lazyOptional = LazyOptional.of(() -> healthData);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == HEALTH_DATA) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (healthData instanceof HealthData){
            ((HealthData)healthData).serializeNBT();
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (healthData instanceof HealthData){
            ((HealthData)healthData).deserializeNBT(nbt);
        }
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }
}

