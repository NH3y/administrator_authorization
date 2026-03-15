package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class AuthorizePlayerProcedure {
    public static void execute(Entity entity) {
        if (entity == null)
            return;
        if (entity.hasPermissions(2)) {
            if (entity instanceof Player) {
                ((EntityAccess) entity).administrator_authorization$setAuthorization();
            }

        }
    }
}
