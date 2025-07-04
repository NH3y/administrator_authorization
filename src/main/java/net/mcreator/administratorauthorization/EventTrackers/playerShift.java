package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.entity.player.PlayerEvent.Clone;

import java.util.logging.Logger;

@Mod.EventBusSubscriber
public class playerShift {
    static Logger logger = Logger.getLogger("Event_Clone");
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(Clone event){
        event.getOriginal().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(oldData -> event.getEntity().getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(newData -> newData.setRouterIndex(oldData.getRouterIndex())));
        final boolean protect = ((EntityAccess) event.getOriginal()).administrator_authorization$getAuthorization();
        if(protect){
            if(event.isWasDeath()){
                event.setCanceled(protect);
                logger.info("Block Death");
            }else {
                ((EntityAccess) event.getEntity()).administrator_authorization$setAuthorization();
            }
            ((LivingEntityAccess) event.getEntity()).administrator_authorization$setAttributes(Attributes.MAX_HEALTH,20);
        }
    }
}
