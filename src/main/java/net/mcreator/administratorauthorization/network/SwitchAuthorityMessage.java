
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.procedures.CallAuthoritySwitchProcedure;
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

public record SwitchAuthorityMessage(int messageType, int pressdms) implements CustomPacketPayload {
    public static final Type<SwitchAuthorityMessage> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "switch_authority"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SwitchAuthorityMessage> STREAM_CODEC = StreamCodec.of((buffer, message) -> {
        buffer.writeVarInt(message.messageType());
        buffer.writeVarInt(message.pressdms());
    }, (buffer) -> new SwitchAuthorityMessage(buffer.readVarInt(), buffer.readVarInt()));

    public static void handleData(final SwitchAuthorityMessage message, final IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            context.enqueueWork(() -> {
            }).exceptionally(e -> {
                context.connection().disconnect(Component.literal(e.getMessage()));
                return null;
            });
        }
    }

    public static void pressAction(Player entity, int type, int pressedms) {
        Level world = entity.level();
        // security measure to prevent arbitrary chunk generation
        if (!world.hasChunk(entity.blockPosition().getX(), entity.blockPosition().getZ()))
            return;
        if (type == 0) {

            CallAuthoritySwitchProcedure.execute(entity);
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
