package net.mcreator.administratorauthorization.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StatusOverlay implements LayeredDraw.Layer {
    private final Minecraft minecraft;

    public StatusOverlay() {
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        if (!AAAuthorizationConfiguration.SHOW_ADMIN.get()) return;

        PoseStack pose = guiGraphics.pose();

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        pose.pushPose();
        pose.translate(centerX, centerY, 0);

        LocalPlayer player = minecraft.player;
        if (player != null && ((EntityAccess) player).administrator_authorization$getAuthorization()) {
            int width = minecraft.font.width("Admin");
            guiGraphics.drawString(
                    minecraft.font,
                    Component.literal("Admin"),
                    centerY - width / 2,
                    centerY - 10,
                    0xFFFFFF00
            );
        }

        pose.popPose();
    }
}
