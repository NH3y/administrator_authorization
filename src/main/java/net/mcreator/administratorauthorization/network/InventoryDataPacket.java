package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.capabilities.InventoryDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class InventoryDataPacket {
    private final int slot;

    public InventoryDataPacket(int slot) {
        this.slot = slot;
    }

    public InventoryDataPacket(){
        this.slot = Integer.MAX_VALUE;
    }

    public static void encode(InventoryDataPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.slot);
    }

    public static InventoryDataPacket decode(FriendlyByteBuf buf) {
        return new InventoryDataPacket(buf.readInt());
    }

    public static void handle(InventoryDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(InventoryDataProvider.SLOT_DATA).ifPresent(data -> data.setSlotIndex(msg.slot));
            }
            ctx.get().setPacketHandled(true);
        });
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event){
        AdministratorAuthorizationMod.addNetworkMessage(
                InventoryDataPacket.class
                , InventoryDataPacket::encode
                , InventoryDataPacket::decode
                , InventoryDataPacket::handle
        );
        System.out.println("Register Message");
    }
}
