package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.classes.TerminalClassFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.*;
import org.jetbrains.annotations.UnknownNullability;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

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

    @Unique
    private final TerminalClassFactory administrator_authorization$factory = TerminalClassFactory.getInstance();

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

    @ModifyVariable(method = "addNewEntityWithoutEvent", at = @At("HEAD"), argsOnly = true, index = 1, remap = false)
    public EntityAccess addNewEntityWithoutEvent(EntityAccess value) {
        if (!(value instanceof Entity entity)) return value;
        if (administrator_authorization$factory.hasProxyFor(entity.getClass()) && entity.level() instanceof ServerLevel serverLevel) {
            return administrator_authorization$factory.createProxy(entity, serverLevel);
        }
        return value;
    }
}
