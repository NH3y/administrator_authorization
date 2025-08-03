package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.IHealthData;
import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.mcreator.administratorauthorization.capabilities.HealthDataProvider;
import net.mcreator.administratorauthorization.capabilities.InventoryDataProvider;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
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
    public static final Capability<IHealthData> HEALTH_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
    public static final Capability<IHealthData> INVENTORY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    @SubscribeEvent
    public static void attachCapabilities(AttachCapabilitiesEvent<Entity> event){
        if (event.getObject() instanceof Player){
            event.addCapability(new ResourceLocation(AdministratorAuthorizationMod.MODID, "router_data"),
                    new RouterDataProvider());

            event.addCapability(new ResourceLocation(AdministratorAuthorizationMod.MODID, "slot_data"),
                    new InventoryDataProvider());
        }

        if (event.getObject() instanceof LivingEntity){
            event.addCapability(new ResourceLocation(AdministratorAuthorizationMod.MODID, "health_data"),
                    new HealthDataProvider());
        }
    }
}
