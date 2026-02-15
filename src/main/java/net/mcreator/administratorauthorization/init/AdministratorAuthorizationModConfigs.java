package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.configuration.AAInterceptorConfiguration;
import net.mcreator.administratorauthorization.configuration.AASecurityConfiguration;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLConstructModEvent;

@EventBusSubscriber(modid = AdministratorAuthorizationMod.MODID)
public class AdministratorAuthorizationModConfigs {
    @SubscribeEvent
    public static void register(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ModContainer activeContainer = ModLoadingContext.get().getActiveContainer();
            activeContainer.registerConfig(ModConfig.Type.COMMON, AADestroyerConfiguration.SPEC, "AA-RealityDestroyer(RD).toml");
            activeContainer.registerConfig(ModConfig.Type.COMMON, AAAuthorizationConfiguration.SPEC, "AA-Authorization.toml");
            activeContainer.registerConfig(ModConfig.Type.COMMON, AAInterceptorConfiguration.SPEC, "AA-Interceptor.toml");
            activeContainer.registerConfig(ModConfig.Type.COMMON, AASecurityConfiguration.SPEC, "AA-Security.toml");
        });
    }
}
