package net.mcreator.administratorauthorization.eventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.client.screens.StatusOverlay;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(Dist.CLIENT)
public class OverlayHandler {
    private static StatusOverlay statusOverlay;

    @SubscribeEvent
    public static void register(RegisterGuiLayersEvent event) {
        statusOverlay = new StatusOverlay();

        event.registerAbove(
                VanillaGuiLayers.HOTBAR,
                ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "status"),
                statusOverlay
        );
    }
}
