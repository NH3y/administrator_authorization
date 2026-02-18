package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.entity.Entity;

public interface CallbackAccess {
    void administrator_authorization$forceOnRemove(Entity.RemovalReason reason);
}
