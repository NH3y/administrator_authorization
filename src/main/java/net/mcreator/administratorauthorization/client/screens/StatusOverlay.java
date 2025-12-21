package net.mcreator.administratorauthorization.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class StatusOverlay implements LayeredDraw.Layer {
    private final Minecraft minecraft;

    public StatusOverlay() {
        this.minecraft = Minecraft.getInstance();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, @NotNull DeltaTracker deltaTracker) {
        PoseStack pose = guiGraphics.pose();

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        pose.pushPose();
        pose.translate(centerX, centerY, 0);

        if (minecraft.player != null) {
            boolean switchBool = ((EntityAccess) minecraft.player).administrator_authorization$getSwitch();
            int width = minecraft.font.width(String.valueOf(switchBool));
            guiGraphics.drawString(
                    minecraft.font,
                    Component.literal(String.valueOf(switchBool)),
                    centerY - width / 2,
                    centerY - 10,
                    0xFFFFFF00
            );
        }

        pose.popPose();
    }
}
