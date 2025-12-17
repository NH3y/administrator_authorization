package net.mcreator.administratorauthorization.attachment;

import net.mcreator.administratorauthorization.Interfaces.IInventoryData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class InventorySlotData implements IInventoryData, INBTSerializable<CompoundTag> {
    private int slot = Integer.MAX_VALUE;

    @Override
    public int getSlotIndex() {
        return slot;
    }

    @Override
    public void setSlotIndex(int index) {
        this.slot = index;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("slot_data", slot);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        slot = tag.getInt("slot_data");
    }
}
