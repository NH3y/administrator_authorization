
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record SpecialFunction1Message(int messageType, int pressedms) implements CustomPacketPayload {
    public static final Type<SpecialFunction1Message> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "special_function"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SpecialFunction1Message> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
        buffer.writeVarInt(message.messageType());
        buffer.writeVarInt(message.pressedms());
    }, (buffer) -> new SpecialFunction1Message(buffer.readVarInt(), buffer.readVarInt()));

    public static void handleData(final SpecialFunction1Message message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }

        context.enqueueWork(() -> pressAction(context.player(), message.messageType(),  message.pressedms()));
    }

    public static void pressAction(Player entity, int type, int pressedms) {
        Level world = entity.level();
        // security measure to prevent arbitrary chunk generation
        if (type == 0) {
            ((PlayerAccess) entity).administrator_authorization$setPressAlter(true);
        } else if (type == 1) {
            ((PlayerAccess) entity).administrator_authorization$setPressAlter(false);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
