
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record RouterButtonMessage(int messageType, int pressdms) implements CustomPacketPayload {
    public static final Type<RouterButtonMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "router_button"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RouterButtonMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
        buffer.writeVarInt(message.messageType());
        buffer.writeVarInt(message.pressdms());
    }, (buffer) -> new RouterButtonMessage(buffer.readVarInt(), buffer.readVarInt()));

    public static void handleData(final RouterButtonMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }

    public static void pressAction(Player entity, int type, int pressedms) {
        // security measure to prevent arbitrary chunk generation
        if (!entity.getMainHandItem().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())) return;
        if (entity instanceof LocalPlayerAccess playerAccess) {
            if (type == 0) {
                playerAccess.administrator_authorization$setPressRouter(true);
            }
            if (type == 1) {
                playerAccess.administrator_authorization$setPressRouter(false);
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
