
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.client.Minecraft;
import net.minecraft.client.KeyMapping;

import net.mcreator.administratorauthorization.network.SwitchAuthorityMessage;
import net.mcreator.administratorauthorization.network.SpecialFunction1Message;
import net.mcreator.administratorauthorization.network.RouterButtonMessage;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = {Dist.CLIENT})
public class AdministratorAuthorizationModKeyMappings {
	public static final KeyMapping SWITCH_AUTHORITY = new KeyMapping("key.administrator_authorization.switch_authority", GLFW.GLFW_KEY_UNKNOWN, "key.categories.adm_auth") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new SwitchAuthorityMessage(0, 0));
				SwitchAuthorityMessage.pressAction(Minecraft.getInstance().player, 0, 0);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping SPECIAL_FUNCTION_1 = new KeyMapping("key.administrator_authorization.special_function_1", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.adm_auth") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new SpecialFunction1Message(0, 0));
				SpecialFunction1Message.pressAction(Minecraft.getInstance().player, 0, 0);
				SPECIAL_FUNCTION_1_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - SPECIAL_FUNCTION_1_LASTPRESS);
				AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new SpecialFunction1Message(1, dt));
				SpecialFunction1Message.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	public static final KeyMapping ROUTER_BUTTON = new KeyMapping("key.administrator_authorization.router_button", GLFW.GLFW_KEY_R, "key.categories.misc") {
		private boolean isDownOld = false;

		@Override
		public void setDown(boolean isDown) {
			super.setDown(isDown);
			if (isDownOld != isDown && isDown) {
				AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new RouterButtonMessage(0, 0));
				RouterButtonMessage.pressAction(Minecraft.getInstance().player, 0, 0);
				ROUTER_BUTTON_LASTPRESS = System.currentTimeMillis();
			} else if (isDownOld != isDown && !isDown) {
				int dt = (int) (System.currentTimeMillis() - ROUTER_BUTTON_LASTPRESS);
				AdministratorAuthorizationMod.PACKET_HANDLER.sendToServer(new RouterButtonMessage(1, dt));
				RouterButtonMessage.pressAction(Minecraft.getInstance().player, 1, dt);
			}
			isDownOld = isDown;
		}
	};
	private static long SPECIAL_FUNCTION_1_LASTPRESS = 0;
	private static long ROUTER_BUTTON_LASTPRESS = 0;

	@SubscribeEvent
	public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
		event.register(SWITCH_AUTHORITY);
		event.register(SPECIAL_FUNCTION_1);
		event.register(ROUTER_BUTTON);
	}

	@Mod.EventBusSubscriber({Dist.CLIENT})
	public static class KeyEventListener {
		@SubscribeEvent
		public static void onClientTick(TickEvent.ClientTickEvent event) {
			if (Minecraft.getInstance().screen == null) {
				SWITCH_AUTHORITY.consumeClick();
				SPECIAL_FUNCTION_1.consumeClick();
				ROUTER_BUTTON.consumeClick();
			}
		}
	}
}
