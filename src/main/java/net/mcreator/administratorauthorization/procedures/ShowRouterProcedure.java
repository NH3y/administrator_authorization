package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;

public class ShowRouterProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof PlayerAccess playerAccess && playerAccess.administrator_authorization$isPressRouter();
	}
}
