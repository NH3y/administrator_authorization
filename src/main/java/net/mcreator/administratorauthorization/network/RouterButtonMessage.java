
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
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
public class RouterButtonMessage {
	int type, pressedms;

	public RouterButtonMessage(int type, int pressedms) {
		this.type = type;
		this.pressedms = pressedms;
	}

	public RouterButtonMessage(FriendlyByteBuf buffer) {
		this.type = buffer.readInt();
		this.pressedms = buffer.readInt();
	}

	public static void buffer(RouterButtonMessage message, FriendlyByteBuf buffer) {
		buffer.writeInt(message.type);
		buffer.writeInt(message.pressedms);
	}

	public static void handler(RouterButtonMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> pressAction(Objects.requireNonNull(context.getSender()), message.type, message.pressedms));
		context.setPacketHandled(true);
	}

	public static void pressAction(Player entity, int type, int pressedms) {
		Level world = entity.level();
        // security measure to prevent arbitrary chunk generation
        if (!entity.getMainHandItem().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())) return;
		if ( entity instanceof LocalPlayerAccess playerAccess) {
			if (type == 0) {
				playerAccess.administrator_authorization$setPressRouter(true);
			}
			if (type == 1) {
				playerAccess.administrator_authorization$setPressRouter(false);
			}
		}
	}

	@SubscribeEvent
	public static void registerMessage(FMLCommonSetupEvent event) {
		AdministratorAuthorizationMod.addNetworkMessage(RouterButtonMessage.class, RouterButtonMessage::buffer, RouterButtonMessage::new, RouterButtonMessage::handler);
	}
}
