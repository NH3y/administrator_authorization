package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.procedures.DestroyRouterProcedure;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickItem;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class InteractEvent {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interact(EntityInteract event) {
        if (AADestroyerConfiguration.ACCEPT_INTERACT.get() && event.getItemStack().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())) {
            DestroyRouterProcedure.execute(event.getTarget(), event.getEntity(), event.getLevel());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void interactAir(RightClickItem event) {
        if (event.getItemStack().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get()) && (event.isCanceled() || event.getCancellationResult().equals(InteractionResult.FAIL))) {
            event.setCanceled(false);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }
}
