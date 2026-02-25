
package net.mcreator.administratorauthorization.client.screens;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.procedures.ReturnCurrentIndexProcedure;
import net.mcreator.administratorauthorization.procedures.ShowRouterProcedure;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber({Dist.CLIENT})
public class RouterScreenOverlay {
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void eventHandler(RenderGuiEvent.Pre event) {
        int w = event.getWindow().getGuiScaledWidth();
        int h = event.getWindow().getGuiScaledHeight();
        Player entity = Minecraft.getInstance().player;
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        if (ShowRouterProcedure.execute(entity)) {
            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.blit(new ResourceLocation("administrator_authorization:textures/screens/circle.png"), w / 2 - 96, h / 2 - 96, 0, 0, 192, 192, 192, 192);

            Font font = Minecraft.getInstance().font;
            if (!((PlayerAccess) entity).administrator_authorization$isPressAlter()) {
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_damage"), w / 2 + 17, h / 2 - 67, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_kill"), w / 2 + 57, h / 2 - 31, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_defeat"), w / 2 + 57, h / 2 + 23, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_annihilate"), w / 2 + 9, h / 2 + 68, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_obliterate"), w / 2 - 55, h / 2 + 68, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_disintegrate"), w / 2 - 89, h / 2 + 23, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_selfDestruct"), w / 2 - 95, h / 2 - 23, -1, false);
                guiGraphics.drawString(font, "יוםהדין", w / 2 - 47, h / 2 - 67, -1, false);
            } else {
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_weaken"), w / 2 + 17, h / 2 - 67, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_disable"), w / 2 + 37, h / 2 - 31, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_neutralize"), w / 2 + 37, h / 2 + 23, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_damnatio_memoriae_A"), w / 2 + 9, h / 2 + 54, -1, false);
                guiGraphics.drawString(font, Component.translatable("gui.administrator_authorization.router_screen.label_damnatio_memoriae_B"), w / 2 + 9, h / 2 + 64, -1, false);
            }
            guiGraphics.drawString(font,
                    ReturnCurrentIndexProcedure.execute(entity), w / 2, h / 2 - 112, -1, false);
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.enableDepthTest();
            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
    }
}