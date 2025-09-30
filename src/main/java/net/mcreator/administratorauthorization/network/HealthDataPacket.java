package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.capabilities.HealthDataProvider;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public record HealthDataPacket(float healthLimit, boolean healthLock) {

    public HealthDataPacket() {
        this(Float.MAX_VALUE, false);
    }

    public static void encode(HealthDataPacket msg, FriendlyByteBuf buf) {
        buf.writeFloat(msg.healthLimit);
        buf.writeBoolean(msg.healthLock);
    }

    public static HealthDataPacket decode(FriendlyByteBuf buf) {
        return new HealthDataPacket(buf.readFloat(), buf.readBoolean());
    }

    public static void handle(HealthDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                player.getCapability(HealthDataProvider.HEALTH_DATA).ifPresent(data -> {
                            data.setHealthLimit(msg.healthLimit);
                            data.setHealthLock(msg.healthLock);
                        }
                );
            }
        });
        ctx.get().setPacketHandled(true);
    }

    @SubscribeEvent
    public static void registerMessage(FMLCommonSetupEvent event) {
        AdministratorAuthorizationMod.addNetworkMessage(
                HealthDataPacket.class
                , HealthDataPacket::encode
                , HealthDataPacket::decode
                , HealthDataPacket::handle
        );
        System.out.println("Register Message");
    }
}