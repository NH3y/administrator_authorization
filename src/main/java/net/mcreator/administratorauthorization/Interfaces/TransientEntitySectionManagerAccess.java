package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.level.entity.LevelCallback;

public interface TransientEntitySectionManagerAccess<T> {
    LevelCallback<T> administrator_authorization$getLevelCallbackBack();
}
