
/*
 *	MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.network.RouterButtonMessage;
import net.mcreator.administratorauthorization.network.SpecialFunction1Message;
import net.mcreator.administratorauthorization.network.SwitchAuthorityMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber( value = {Dist.CLIENT})
public class AdministratorAuthorizationModKeyMappings {
    public static final KeyMapping SWITCH_AUTHORITY = new KeyMapping("key.administrator_authorization.switch_authority", GLFW.GLFW_KEY_UNKNOWN, "key.categories.adm_auth") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (isDownOld != isDown && isDown) {
                PacketDistributor.sendToServer(new SwitchAuthorityMessage(0, 0));
                if (Minecraft.getInstance().player != null) {
                    SwitchAuthorityMessage.pressAction(Minecraft.getInstance().player, 0, 0);
                }
            }
            isDownOld = isDown;
        }
    };
    public static final KeyMapping SPECIAL_FUNCTION_1 = new KeyMapping("key.administrator_authorization.special_function_1", GLFW.GLFW_KEY_LEFT_SHIFT, "key.categories.adm_auth") {
        private boolean isDownOld = false;

        @Override
        public void setDown(boolean isDown) {
            super.setDown(isDown);
            if (Minecraft.getInstance().player == null) return;
            if (isDownOld != isDown && isDown) {
                PacketDistributor.sendToServer(new SpecialFunction1Message(0, 0));
                SpecialFunction1Message.pressAction(Minecraft.getInstance().player, 0, 0);
                SPECIAL_FUNCTION_1_LASTPRESS = System.currentTimeMillis();
            } else if (isDownOld != isDown) {
                int dt = (int) (System.currentTimeMillis() - SPECIAL_FUNCTION_1_LASTPRESS);
                PacketDistributor.sendToServer(new SpecialFunction1Message(1, dt));
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
            if (Minecraft.getInstance().player == null) return;
            if (isDownOld != isDown && isDown) {
                PacketDistributor.sendToServer(new RouterButtonMessage(0, 0));
                RouterButtonMessage.pressAction(Minecraft.getInstance().player, 0, 0);
                ROUTER_BUTTON_LASTPRESS = System.currentTimeMillis();
            } else if (isDownOld != isDown) {
                int dt = (int) (System.currentTimeMillis() - ROUTER_BUTTON_LASTPRESS);
                PacketDistributor.sendToServer(new RouterButtonMessage(1, dt));
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

    @EventBusSubscriber({Dist.CLIENT})
    public static class KeyEventListener {
        @SubscribeEvent
        public static void onClientTick(ClientTickEvent.Post event) {
            if (Minecraft.getInstance().screen == null) {
                SWITCH_AUTHORITY.consumeClick();
                SPECIAL_FUNCTION_1.consumeClick();
                ROUTER_BUTTON.consumeClick();
            }
        }
    }
}
