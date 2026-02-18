package net.mcreator.administratorauthorization.mixins;

import net.minecraft.world.level.entity.*;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransientEntitySectionManager.class)
public class TransientEntitySectionManagerMixin<T extends EntityAccess> implements net.mcreator.administratorauthorization.Interfaces.TransientEntitySectionManagerAccess<T> {
    @Shadow
    @Final
    LevelCallback<T> callbacks;

    @Shadow
    @Final
    EntityLookup<T> entityStorage;

    @Shadow
    @Final
    EntitySectionStorage<T> sectionStorage;

    @Override
    public LevelCallback<T> administrator_authorization$getLevelCallbackBack() {
        return this.callbacks;
    }

    @Override
    public EntityLookup<T> administrator_authorization$getEntityStorage() {
        return this.entityStorage;
    }

    @Override
    public void administrator_authorization$removeSectionIfEmpty(long pSection, EntitySection<T> pEntitySection) {
        if (pEntitySection.isEmpty()) {
            this.sectionStorage.remove(pSection);
        }
    }
}
