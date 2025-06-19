
package net.mcreator.administratorauthorization.network;

import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.administratorauthorization.procedures.CallAuthoritySwitchProcedure;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SwitchAuthorityMessage {
	int type, pressedms;

	public SwitchAuthorityMessage(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public SwitchAuthorityMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(SwitchAuthorityMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(SwitchAuthorityMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			pressAction(Objects.requireNonNull(context.getSender()), message.type, message.pressedms);
		});
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
		double x = entity.getX();
		double y = entity.getY();
		double z = entity.getZ();
		// security measure to prevent arbitrary chunk generation
		if (!world.hasChunkAt(entity.blockPosition()))
			return;
		if (type == 0) {

			CallAuthoritySwitchProcedure.execute(entity);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AdministratorAuthorizationMod.addNetworkMessage(SwitchAuthorityMessage.class, SwitchAuthorityMessage::buffer, SwitchAuthorityMessage::new, SwitchAuthorityMessage::handler);
	}
}
