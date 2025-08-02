package net.mcreator.administratorauthorization.Interfaces;

import it.unimi.dsi.fastutil.objects.ObjectCollection;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;

public interface EntityDataAccess {
    <T> void administrator_authorization$forceSet(EntityDataAccessor<T> pKey, T pValue);

    <T> SynchedEntityData.DataItem<T> administrator_authorization$publicGetItem(EntityDataAccessor<T> pKey);

    ObjectCollection<SynchedEntityData.DataItem<?>> administrator_authorization$getAllItems();
}
