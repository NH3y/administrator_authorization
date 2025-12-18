package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.procedures.HealthDataOperant;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record HealthDataPacket(float healthLimit, boolean healthLock) implements CustomPacketPayload {
    public static final Type<HealthDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "health_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, HealthDataPacket> STREAM_CODEC = StreamCodec.of((buffer, message) ->{
        buffer.writeFloat(message.healthLimit);
        buffer.writeBoolean(message.healthLock);
    }, (buffer) -> new HealthDataPacket(buffer.readFloat(), buffer.readBoolean()));


    public static void handleData(final HealthDataPacket message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
        Player player = context.player();
        HealthDataOperant.updateHealthLimit(player, message.healthLimit);
        HealthDataOperant.updateHealthLock(player, message.healthLock);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}