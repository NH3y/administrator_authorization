package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AdministratorAuthorizationMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class attachCapability {
    public static final Capability<IRouterData> ROUTER_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player){
            event.addCapability(new ResourceLocation(AdministratorAuthorizationMod.MODID, "router_data"),
                    new RouterDataProvider());
        }
    }
}
