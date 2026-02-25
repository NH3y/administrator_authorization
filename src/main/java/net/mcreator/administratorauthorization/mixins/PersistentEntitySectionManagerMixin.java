package net.mcreator.administratorauthorization.mixins;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.*;
import org.jetbrains.annotations.UnknownNullability;
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

    @Shadow
    @Final
    private EntityLookup<T> visibleEntityStorage;

    @Shadow
    @Final
    EntitySectionStorage<T> sectionStorage;

    @Shadow
    @Final
    private LevelEntityGetter<T> entityGetter;

    @Override
    public LevelCallback<T> administrator_authorization$getLevelCallbackBack() {
        return this.callbacks;
    }

    @Override
    public void administrator_authorization$removeUuid(@UnknownNullability Entity entity) {
        this.knownUuids.remove(entity.getUUID());
    }

    @Override
    public void administrator_authorization$stopTicking(T entity) {
        this.callbacks.onTickingEnd(entity);
    }

    @Override
    public void administrator_authorization$stopTracking(T entity) {
        this.callbacks.onTrackingEnd(entity);
        this.visibleEntityStorage.remove(entity);
    }

    @Override
    public void administrator_authorization$removeSectionIfEmpty(long pSectionKey, EntitySection<T> pSection) {
        if (pSection.isEmpty()) {
            this.sectionStorage.remove(pSectionKey);
        }
    }
}
