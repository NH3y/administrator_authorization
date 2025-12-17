package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.procedures.DestroyRouterProcedure;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class InteractEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interact(PlayerInteractEvent.EntityInteract event) {
        if (AADestroyerConfiguration.ACCEPT_INTERACT.get() && event.getItemStack().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())) {
            DestroyRouterProcedure.execute(event.getTarget(), event.getEntity(), event.getLevel());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void interactAir(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get()) && (event.isCanceled() || event.getCancellationResult().equals(InteractionResult.FAIL))) {
            event.setCanceled(false);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
