package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.CallbackAccess;
import net.mcreator.administratorauthorization.Interfaces.TransientEntitySectionManagerAccess;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import net.minecraft.world.level.entity.Visibility;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import static net.minecraft.world.level.entity.EntityInLevelCallback.NULL;

@Mixin(targets = "net.minecraft.world.level.entity.TransientEntitySectionManager$Callback")
public class TransientCallbackMixin<T extends EntityAccess> implements CallbackAccess {
    @Shadow
    private EntitySection<T> currentSection;

    @Shadow
    @Final
    private T entity;

    @Shadow
    @Final
    TransientEntitySectionManager<T> this$0;

    @Shadow
    private long currentSectionKey;

    @Override
    public void administrator_authorization$forceOnRemove(Entity.RemovalReason reason) {
        TransientEntitySectionManagerAccess<T> managerAccess = (TransientEntitySectionManagerAccess<T>) this$0;
        if (!this.currentSection.remove(this.entity)) {
            AdministratorAuthorizationMod.LOGGER.warn("Entity {} wasn't found in section {} (destroying due to {})", this.entity, SectionPos.of(this.currentSectionKey), reason);
        }

        Visibility visibility = this.currentSection.getStatus();
        if (visibility.isTicking() || this.entity.isAlwaysTicking()) {
            managerAccess.administrator_authorization$getLevelCallbackBack().onTickingEnd(this.entity);
        }

        managerAccess.administrator_authorization$getLevelCallbackBack().onTrackingEnd(this.entity);
        managerAccess.administrator_authorization$getLevelCallbackBack().onDestroyed(this.entity);
        managerAccess.administrator_authorization$getEntityStorage().remove(this.entity);
        this.entity.setLevelCallback(NULL);
        managerAccess.administrator_authorization$removeSectionIfEmpty(this.currentSectionKey, this.currentSection);
    }
}
