package net.mcreator.administratorauthorization.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;
import java.util.UUID;

@Mixin(PersistentEntitySectionManager.class)
public abstract class PersistentEntitySectionManagerMixin<T extends EntityAccess> implements net.mcreator.administratorauthorization.Interfaces.PersistentEntitySectionManagerAccess<T> {
    @Shadow
    @Final
    LevelCallback<T> callbacks;

    @Shadow
    @Final
    Set<UUID> knownUuids;

    @Shadow
    protected abstract void requestChunkLoad(long pChunkPosValue);

    @Override
    public LevelCallback<T> administrator_authorization$getLevelCallbackBack(){
        return this.callbacks;
    }

    @Override
    public void administrator_authorization$removeUuid(Entity entity){
        this.knownUuids.remove(entity.getUUID());
    }

    @Override
    public void administrator_authorization$publicRequestChunkLoad(BlockPos blockPos){
        this.requestChunkLoad(ChunkPos.asLong(blockPos));
    }
}
