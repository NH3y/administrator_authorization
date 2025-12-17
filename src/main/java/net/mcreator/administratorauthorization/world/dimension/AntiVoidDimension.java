
package net.mcreator.administratorauthorization.world.dimension;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import org.jetbrains.annotations.NotNull;

public class AntiVoidDimension {
    @EventBusSubscriber
    public static class AntiVoidSpecialEffectsHandler {
        @SubscribeEvent
        @OnlyIn(Dist.CLIENT)
        public static void registerDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
            DimensionSpecialEffects customEffect = new DimensionSpecialEffects(Float.NaN, true, DimensionSpecialEffects.SkyType.NONE, false, false) {
                @Override
                public @NotNull Vec3 getBrightnessDependentFogColor(@NotNull Vec3 color, float sunHeight) {
                    return color;
                }

                @Override
                public boolean isFoggyAt(int x, int y) {
                    return false;
                }
            };
            event.register(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "anti_void"), customEffect);
        }
    }
}
