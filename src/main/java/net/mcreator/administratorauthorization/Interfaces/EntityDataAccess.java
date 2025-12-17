package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

import java.util.List;
import java.util.Set;

public interface EntityDataAccess {
    <T> void administrator_authorization$forceSet(EntityDataAccessor<T> pKey, T pValue);

    <T> SynchedEntityData.DataItem<T> administrator_authorization$publicGetItem(EntityDataAccessor<T> pKey);

    List<SynchedEntityData.DataItem<?>> administrator_authorization$getAllItems();

    Set<Integer> Administrator_authorization$getBannedId();
}
