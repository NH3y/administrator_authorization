package net.mcreator.administratorauthorization.Interfaces;

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
}
