package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.entity.Entity;

public interface EntityAccess {
    default boolean administrator_authorization$getAuthorization() {
        return false;
    }

    default boolean administrator_authorization$getSwitch() {
        return true;
    }

    void administrator_authorization$setAuthorization();

    void administrator_authorization$setSwitch(boolean select);

    void administrator_authorization$forceRemove();

    void administrator_authorization$forceSetRemoved(Entity.RemovalReason pRemovalReason);

    boolean Administrator_authorization$isForgotten();

    void Administrator_authorization$setForgotten(boolean administrator_authorization$forgotten);

    boolean administrator_authorization$isRejectSave();

    void administrator_authorization$setRejectSave(boolean administrator_authorization$rejectSave);

    boolean administrator_authorization$isEmergency();

    void administrator_authorization$setEmergency(boolean administrator_authorization$emergency);

    void administrator_authorization$tickEmergency();
}
