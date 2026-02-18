package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.PersistentEntitySectionManagerAccess;
import net.minecraft.world.level.entity.*;

import net.mcreator.administratorauthorization.Interfaces.CallbackAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(targets = "net.minecraft.world.level.entity.PersistentEntitySectionManager$Callback")
public class PersistentCallbackMixin<T extends EntityAccess> implements CallbackAccess {

    @Shadow
    private EntitySection<T> currentSection;

    @Shadow
    @Final
    private T entity;

    @Shadow
    private long currentSectionKey;

    @Shadow
    @Final
    PersistentEntitySectionManager<T> this$0;

    @Override
    public void administrator_authorization$forceOnRemove(Entity.RemovalReason reason) {
        PersistentEntitySectionManagerAccess<T> managerAccess = (PersistentEntitySectionManagerAccess<T>) this$0;
        if (entity instanceof Entity realEntity) {
            if (!this.currentSection.remove(this.entity)) {
                AdministratorAuthorizationMod.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, SectionPos.of(this.currentSectionKey), reason);
            }

            Visibility visibility = administrator_authorization$getEffectiveStatus(this.entity, this.currentSection.getStatus());
            if (visibility.isTicking()) {
                managerAccess.administrator_authorization$stopTicking(this.entity);
            }

            if (visibility.isAccessible()) {
                managerAccess.administrator_authorization$stopTracking(this.entity);
            }

            if (reason.shouldDestroy()) {
                managerAccess.administrator_authorization$getLevelCallbackBack().onDestroyed(this.entity);
            }

            managerAccess.administrator_authorization$removeUuid(realEntity);
            this.entity.setLevelCallback(EntityInLevelCallback.NULL);
            managerAccess.administrator_authorization$removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
        }
    }

    @Unique
    private static <T extends EntityAccess> Visibility administrator_authorization$getEffectiveStatus(T pEntity, Visibility pVisibility) {
        return pEntity.isAlwaysTicking() ? Visibility.TICKING : pVisibility;
    }
}
