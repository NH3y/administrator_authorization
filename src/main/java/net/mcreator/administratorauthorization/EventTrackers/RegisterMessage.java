package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.network.*;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber
public class RegisterMessage {
    @SubscribeEvent
    public static void handleRegisterEvent(FMLCommonSetupEvent e){
        System.out.println("RegisterEvent received");
        AdministratorAuthorizationMod.addNetworkMessage(HealthDataPacket.TYPE, HealthDataPacket.STREAM_CODEC, HealthDataPacket::handleData);
        AdministratorAuthorizationMod.addNetworkMessage(RouterButtonMessage.TYPE, RouterButtonMessage.STREAM_CODEC, RouterButtonMessage::handleData);
        AdministratorAuthorizationMod.addNetworkMessage(InventoryDataPacket.TYPE, InventoryDataPacket.STREAM_CODEC, InventoryDataPacket::handleData);
        AdministratorAuthorizationMod.addNetworkMessage(RouterIndexPacket.TYPE, RouterIndexPacket.STREAM_CODEC, RouterIndexPacket::handleData);
        AdministratorAuthorizationMod.addNetworkMessage(SpecialFunction1Message.TYPE, SpecialFunction1Message.STREAM_CODEC, SpecialFunction1Message::handleData);
        AdministratorAuthorizationMod.addNetworkMessage(SwitchAuthorityMessage.TYPE, SwitchAuthorityMessage.STREAM_CODEC, SwitchAuthorityMessage::handleData);
    }
}
