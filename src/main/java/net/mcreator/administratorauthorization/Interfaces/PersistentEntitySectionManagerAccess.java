package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.LevelCallback;

public interface PersistentEntitySectionManagerAccess<T extends EntityAccess> {
    LevelCallback<T> administrator_authorization$getLevelCallbackBack();

    void administrator_authorization$removeUuid(Entity entity);

    void administrator_authorization$publicRequestChunkLoad(BlockPos blockPos);
}
