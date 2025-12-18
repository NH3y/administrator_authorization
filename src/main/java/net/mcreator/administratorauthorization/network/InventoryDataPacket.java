package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record InventoryDataPacket(int slot) implements CustomPacketPayload {
    public static final Type<InventoryDataPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "inventory_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, InventoryDataPacket> STREAM_CODEC = StreamCodec.of((buffer, message) -> buffer.writeInt(message.slot()), (buffer) -> new InventoryDataPacket(buffer.readInt()));

    public static void handleData(final InventoryDataPacket message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
        context.player().getData(
                AttachmentRegistry.INVENTORY_SLOT_DATA
        ).setSlotIndex(message.slot());
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
