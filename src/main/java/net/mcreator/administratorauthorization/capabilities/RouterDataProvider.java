package net.mcreator.administratorauthorization.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RouterDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {

    public static final Capability<IRouterData> ROUTER_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final IRouterData routerData = new RouterData();
    private final LazyOptional<IRouterData> lazyOptional = LazyOptional.of(() -> routerData);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == ROUTER_DATA) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (routerData instanceof RouterData){
            ((RouterData)routerData).serializeNBT();
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (routerData instanceof RouterData){
            ((RouterData)routerData).deserializeNBT(nbt);
        }
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }
}
