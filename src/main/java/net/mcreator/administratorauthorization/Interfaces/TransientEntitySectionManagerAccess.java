package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntityLookup;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.LevelCallback;

public interface TransientEntitySectionManagerAccess<T extends EntityAccess> {
    LevelCallback<T> administrator_authorization$getLevelCallbackBack();

    EntityLookup<T> administrator_authorization$getEntityStorage();

    void administrator_authorization$removeSectionIfEmpty(long pSection, EntitySection<T> pEntitySection);
}
