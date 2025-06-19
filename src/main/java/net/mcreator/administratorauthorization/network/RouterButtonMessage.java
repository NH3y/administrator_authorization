
package net.mcreator.administratorauthorization.network;

import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;
import net.minecraft.network.FriendlyByteBuf;

import net.mcreator.administratorauthorization.procedures.AuthorizePlayerProcedure;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

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
		context.enqueueWork(() -> {
			pressAction(context.getSender(), message.type, message.pressedms);
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
		if ( entity instanceof PlayerAccess playerAccess) {
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
