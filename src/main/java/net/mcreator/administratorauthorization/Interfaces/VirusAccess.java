package net.mcreator.administratorauthorization.Interfaces;

import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.Set;

public interface VirusAccess {
    boolean administrator_authorization$checkVirus();

    Set<Class<? extends Event>> administrator_authorization$getAllSuperClass(Class<? extends EntityEvent> target);
}
