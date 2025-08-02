package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

@Mod.EventBusSubscriber(modid = AdministratorAuthorizationMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class AdministratorAuthorizationModConfigs {
	@SubscribeEvent
	public static void register(FMLConstructModEvent event) {
		event.enqueueWork(() -> {
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AADestroyerConfiguration.SPEC, "AA-RealityDestroyer(RD).toml");
			ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AAAuthorizationConfiguration.SPEC, "AA-Authorization.toml");
		});
	}
}
