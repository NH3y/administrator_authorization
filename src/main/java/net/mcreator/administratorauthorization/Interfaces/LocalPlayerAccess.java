package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.client.Minecraft;

public interface LocalPlayerAccess {
    Minecraft administrator_authorization$getMinecraft();

    void administrator_authorization$setPressRouter(boolean press);

    boolean administrator_authorization$isPressRouter();
}
