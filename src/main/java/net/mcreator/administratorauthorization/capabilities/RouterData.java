package net.mcreator.administratorauthorization.capabilities;

import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.minecraft.nbt.CompoundTag;

public class RouterData implements IRouterData{
    private int RouterIndex = 0;

    @Override
    public int getRouterIndex(){
        return RouterIndex;
    }

    @Override
    public void setRouterIndex(int index){
        this.RouterIndex = index;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("routerIndex", RouterIndex);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag){
        RouterIndex = tag.getInt("routerIndex");
    }
}
