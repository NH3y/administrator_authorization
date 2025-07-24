package net.mcreator.administratorauthorization.capabilities;

import net.mcreator.administratorauthorization.Interfaces.IInventoryData;
import net.minecraft.nbt.CompoundTag;

public class InventorySlotData implements IInventoryData {
    private int slot;

    @Override
    public int getSlotIndex(){
        return slot;
    }

    @Override
    public void setSlotIndex(int index){
        this.slot = index;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("slot_data", slot);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag){
        slot = tag.getInt("slot_data");
    }
}
