package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;

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

    boolean administrator_authorization$isForgotten();

    void administrator_authorization$setForgotten(boolean administrator_authorization$forgotten);

    boolean administrator_authorization$isRejectSave();

    void administrator_authorization$setRejectSave(boolean administrator_authorization$rejectSave);

    void administrator_authorization$setAttackedCode(int attackedCode);

    int administrator_authorization$getAttackedCode();

    boolean administrator_authorization$isFreeze();

    void administrator_authorization$setFreeze(boolean administrator_authorization$freeze);

    EntityInLevelCallback administrator_authorization$getLevelCallback();
}
