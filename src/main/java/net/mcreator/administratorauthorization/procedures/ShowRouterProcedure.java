package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class ShowRouterProcedure {
    public static boolean execute(Entity entity) {
        if (entity == null) {
            return false;
        }
        if (entity instanceof Player player && player.hasContainerOpen()) return false;
        return entity instanceof LocalPlayerAccess playerAccess && playerAccess.administrator_authorization$isPressRouter();
    }
}
