package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.capabilities.RouterDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RouterIndexPacket {
    private final int routerIndex;

    public RouterIndexPacket(int routerIndex) {
        this.routerIndex = routerIndex;
    }

    public static void encode(RouterIndexPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.routerIndex);
    }

    public static RouterIndexPacket decode(FriendlyByteBuf buf) {
        return new RouterIndexPacket(buf.readInt());
    }

    public static void handle(RouterIndexPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(RouterDataProvider.ROUTER_DATA).ifPresent(data -> data.setRouterIndex(msg.routerIndex));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event){
        AdministratorAuthorizationMod.addNetworkMessage(
                RouterIndexPacket.class
                ,RouterIndexPacket::encode
                ,RouterIndexPacket::decode
                ,RouterIndexPacket::handle
        );
        System.out.println("Register Message");
    }
}