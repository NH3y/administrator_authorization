package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;

public interface ServerLevelAccess {
    PersistentEntitySectionManager<Entity> administrator_authorization$getEntityManager();

    EntityTickList administrator_authorization$getEntityTickList();
}
