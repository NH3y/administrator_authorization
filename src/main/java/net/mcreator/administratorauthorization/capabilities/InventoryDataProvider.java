package net.mcreator.administratorauthorization.capabilities;

import net.mcreator.administratorauthorization.Interfaces.IInventoryData;
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

public class InventoryDataProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<IInventoryData> SLOT_DATA = CapabilityManager.get(new CapabilityToken<>() {
    });

    private final IInventoryData slotData = new InventorySlotData();
    private final LazyOptional<IInventoryData> lazyOptional = LazyOptional.of(() -> slotData);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side){
        if(cap == SLOT_DATA) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (slotData instanceof InventorySlotData){
            ((InventorySlotData)slotData).serializeNBT();
        }
        return new CompoundTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (slotData instanceof InventorySlotData){
            ((InventorySlotData)slotData).deserializeNBT(nbt);
        }
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }
}
