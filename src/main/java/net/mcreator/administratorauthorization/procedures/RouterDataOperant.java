package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.minecraft.world.entity.player.Player;

public class RouterDataOperant {
    public static int getPlayerRouterIndex(Player player) {
        return player.getData(AttachmentRegistry.ROUTER_DATA.get()).getRouterIndex();
    }

    public static void updatePlayerRouterIndex(Player player, int newInt) {
        player.getData(AttachmentRegistry.ROUTER_DATA.get()).setRouterIndex(newInt);
    }
}
