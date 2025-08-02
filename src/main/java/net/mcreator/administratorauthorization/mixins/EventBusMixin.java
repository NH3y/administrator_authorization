package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.VirusAccess;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.IEventBusInvokeDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(EventBus.class)
public class EventBusMixin {

    @Inject(method = "post(Lnet/minecraftforge/eventbus/api/Event;Lnet/minecraftforge/eventbus/api/IEventBusInvokeDispatcher;)Z", at = @At("HEAD"), remap = false, cancellable = true)
    public void post(Event event, IEventBusInvokeDispatcher wrapper, CallbackInfoReturnable<Boolean> cir){
        if(event instanceof VirusAccess entityEvent && entityEvent.administrator_authorization$checkVirus()){
            Set<Class<? extends Event>> classes = entityEvent.administrator_authorization$getAllSuperClass((Class<? extends EntityEvent>) event.getClass());
            cir.setReturnValue(classes.stream().anyMatch(classA -> classA.getName().toLowerCase().contains("hurt")));
        }
    }
}
