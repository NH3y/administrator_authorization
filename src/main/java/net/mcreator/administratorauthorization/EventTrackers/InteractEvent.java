package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.procedures.DestroyRouterProcedure;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;

@Mod.EventBusSubscriber
public class InteractEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interact(EntityInteract event){
        if (AADestroyerConfiguration.ACCEPT_INTERACT.get() && event.getItemStack().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())){
            DestroyRouterProcedure.execute(event.getTarget(), event.getEntity(), event.getLevel());
        }
    }

}
