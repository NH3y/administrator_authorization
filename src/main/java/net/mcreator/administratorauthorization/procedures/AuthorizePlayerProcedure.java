package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class AuthorizePlayerProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;
        if (entity.hasPermissions(AAAuthorizationConfiguration.REQUIRED_LEVEL.get())) {
            if (entity instanceof Player player) {
                if (player.isLocalPlayer()) {
                    player.level().playLocalSound(player, SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 0.5F, 1.0F);
                    player.displayClientMessage(Component.literal("Player " + player.getDisplayName().getString() + " is now authorized."), false);
                }
                ((EntityAccess) entity).administrator_authorization$setAuthorization();
            }

        }
    }
}
