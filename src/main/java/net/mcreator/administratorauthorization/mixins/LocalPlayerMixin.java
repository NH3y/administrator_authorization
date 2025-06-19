package net.mcreator.administratorauthorization.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public class LocalPlayerMixin implements net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess {
    @Shadow @Final protected Minecraft minecraft;

    @Override
    public Minecraft administrator_authorization$getMinecraft(){
        return this.minecraft;
    }
}
