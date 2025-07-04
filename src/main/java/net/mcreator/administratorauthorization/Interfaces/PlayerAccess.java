package net.mcreator.administratorauthorization.Interfaces;

import net.mcreator.administratorauthorization.classes.PlayerRouter;

public interface PlayerAccess {
    boolean administrator_authorization$isPressAlter();

    void administrator_authorization$setPressAlter(boolean administrator_authorization$pressAlter);

    boolean administrator_authorization$isPressRouter();

    void administrator_authorization$setPressRouter(boolean press);

    PlayerRouter administrator_authorization$getRouter();

    int administrator_authorization$getRDSlot();

    void administrator_authorization$setRDSlot(int administrator_authorization$RDSlot);
}
