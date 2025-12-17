package net.mcreator.administratorauthorization.attachment;

import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.jetbrains.annotations.NotNull;

public class RouterData implements IRouterData, INBTSerializable<CompoundTag> {
    private int RouterIndex = 0;

    @Override
    public int getRouterIndex() {
        return RouterIndex;
    }

    @Override
    public void setRouterIndex(int index) {
        this.RouterIndex = index;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putInt("routerIndex", RouterIndex);
        return compoundTag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, CompoundTag tag) {
        RouterIndex = tag.getInt("routerIndex");
    }
}
