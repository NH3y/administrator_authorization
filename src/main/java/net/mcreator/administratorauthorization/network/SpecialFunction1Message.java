
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

import java.util.Objects;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpecialFunction1Message {
	int type, pressedms;

	public SpecialFunction1Message(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public SpecialFunction1Message(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(SpecialFunction1Message message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(SpecialFunction1Message message, Supplier<NetworkEvent.Context> contextSupplier) {
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
			((PlayerAccess) entity).administrator_authorization$setPressAlter(true);
		}
		if (type == 1) {
			((PlayerAccess) entity).administrator_authorization$setPressAlter(false);
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AdministratorAuthorizationMod.addNetworkMessage(SpecialFunction1Message.class, SpecialFunction1Message::buffer, SpecialFunction1Message::new, SpecialFunction1Message::handler);
	}
}
