package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.IRouterData;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.world.entity.player.Player;

public class RouterDataOperant {
    public static int getPlayerRouterIndex(Player player){
        return player.getCapability(RouterDataProvider.ROUTER_DATA)
                .map(IRouterData::getRouterIndex)
                .orElse(0);
    }

    public static void updatePlayerRouterIndex(Player player, int newInt){
        player.getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(data ->{
            data.setRouterIndex(newInt);
        });
    }
}
