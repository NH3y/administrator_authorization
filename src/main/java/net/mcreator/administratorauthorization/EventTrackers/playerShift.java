package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;

@Mod.EventBusSubscriber
public class playerShift {
    @SubscribeEvent
    public static void onPlayerClone(Clone event){
        event.getOriginal().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(oldData -> {
            event.getEntity().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(newData ->{
                newData.setRouterIndex(oldData.getRouterIndex());
            });
        });
    }
}
