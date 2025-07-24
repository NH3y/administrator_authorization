
package net.mcreator.administratorauthorization.client.screens;

import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.Minecraft;

import net.mcreator.administratorauthorization.procedures.ShowRouterProcedure;
import net.mcreator.administratorauthorization.procedures.ReturnCurrentIndexProcedure;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.platform.GlStateManager;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RouterScreenOverlay {
	@SubscribeEvent(priority = EventPriority.HIGH)
	public static void eventHandler(RenderGuiEvent.Pre event) {
		int w = event.getWindow().getGuiScaledWidth();
		int h = event.getWindow().getGuiScaledHeight();
		Level world = null;
		double x = 0;
		double y = 0;
		double z = 0;
		Player entity = Minecraft.getInstance().player;
		if (entity != null) {
			world = entity.level();
			x = entity.getX();
			y = entity.getY();
			z = entity.getZ();
		}
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
		RenderSystem.enableBlend();
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		if (ShowRouterProcedure.execute(entity)) {
			event.getGuiGraphics().blit(new ResourceLocation("administrator_authorization:textures/screens/circle.png"), w / 2 - 96, h / 2 - 96, 0, 0, 192, 192, 192, 192);

			if(!((PlayerAccess) entity).administrator_authorization$isPressAlter()) {
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_damage"), w / 2 + 17, h / 2 - 67, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_kill"), w / 2 + 57, h / 2 - 31, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_defeat"), w / 2 + 57, h / 2 + 23, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_annihilate"), w / 2 + 9, h / 2 + 68, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_obliterate"), w / 2 - 55, h / 2 + 68, -1, false);
			}else {
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_weaken"), w / 2 + 17, h / 2 - 67, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_disable"), w / 2 + 37, h / 2 - 31, -1, false);
				event.getGuiGraphics().drawString(Minecraft.getInstance().font, Component.translatable("gui.administrator_authorization.router_screen.label_neutralize"), w / 2 + 37, h / 2 + 23, -1, false);
			}
            event.getGuiGraphics().drawString(Minecraft.getInstance().font,
                    ReturnCurrentIndexProcedure.execute(entity), w / 2, h / 2 - 112, -1, false);
            RenderSystem.depthMask(true);
			RenderSystem.defaultBlendFunc();
			RenderSystem.enableDepthTest();
			RenderSystem.disableBlend();
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}
}