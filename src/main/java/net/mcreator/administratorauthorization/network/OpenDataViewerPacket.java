package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.classes.ObjectVault;
import net.mcreator.administratorauthorization.client.screens.DataViewerOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record OpenDataViewerPacket(Object initialObject) implements CustomPacketPayload {
    public static final Type<OpenDataViewerPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "open_data_viewer"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDataViewerPacket> STREAM_CODEC = StreamCodec.of(
            (buffer, message) -> buffer.writeVarInt(ObjectVault.storeObject(message.initialObject)),
            (buffer) -> new OpenDataViewerPacket(ObjectVault.getObject(buffer.readVarInt()))
    );
    public static void handleData(final OpenDataViewerPacket message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.SERVERBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
        Minecraft instance = Minecraft.getInstance();
        if (instance.screen == null) {
            instance.setScreen(
                    new DataViewerOverlay(message.initialObject)
            );
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
