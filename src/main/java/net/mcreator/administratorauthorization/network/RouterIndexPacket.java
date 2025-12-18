package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.procedures.RouterDataOperant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RouterIndexPacket(int routerIndex) implements CustomPacketPayload {
    public static final Type<RouterIndexPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "router_index"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RouterIndexPacket> STREAM_CODEC = StreamCodec.of((buffer, message) -> buffer.writeVarInt(message.routerIndex()), (buffer) -> new RouterIndexPacket(buffer.readVarInt()));

    public static void handleData(final RouterIndexPacket message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
        RouterDataOperant.updatePlayerRouterIndex(context.player(),  message.routerIndex());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}