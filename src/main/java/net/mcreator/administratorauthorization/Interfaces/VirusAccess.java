package net.mcreator.administratorauthorization.Interfaces;


import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.event.entity.EntityEvent;

import java.util.Set;

public interface VirusAccess {
    boolean administrator_authorization$checkVirus();

    Set<Class<? extends Event>> administrator_authorization$getAllSuperClass(Class<? extends EntityEvent> target);
}
