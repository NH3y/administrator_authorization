package net.mcreator.administratorauthorization.classes;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Tracker {
    private static final Map<Class<? extends Event>, TrackingType> registerMap = new HashMap<>();
    public static final Set<Class<? extends Event>> events = new HashSet<>();

    public static void registerCanceled(Class<? extends Event> event){
        TrackingType registry = registerMap.get(event);
        if(registry != null && registry.equals(TrackingType.CANCEL)) return;
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                true,
                event,
                (eventCapture) -> {
                    if(eventCapture.isCancelable()){
                        eventCapture.setCanceled(true);
                    }
                }
                );
        registerMap.put(event, TrackingType.CANCEL);
        events.add(event);
    }

    public static void registerEntityCanceled(Class<? extends EntityEvent> event){
        TrackingType registry = registerMap.get(event);
        if(registry != null && registry.equals(TrackingType.ENTITY_CANCEL)) return;
        MinecraftForge.EVENT_BUS.addListener(
                EventPriority.LOWEST,
                true,
                event,
                (eventCapture) -> {
                    if(((EntityAccess) eventCapture.getEntity()).administrator_authorization$getAuthorization()){
                        if(eventCapture.isCancelable()){
                            eventCapture.setCanceled(true);
                        }else if(eventCapture.hasResult()){
                            eventCapture.setResult(Event.Result.DENY);
                        }
                    }
                }
        );
        registerMap.put(event, TrackingType.ENTITY_CANCEL);
        events.add(event);
    }

    public enum TrackingType {
        ENTITY_CANCEL(true, true),
        CANCEL(false, true);

        private final boolean admin;
        private final boolean canceled;
        TrackingType(boolean adminOnly, boolean justCancel){
            this.admin = adminOnly;
            this.canceled = justCancel;
        }
    }

    public static Map<Class<? extends Event>, TrackingType> getRegisterMap() {
        return registerMap;
    }
}
