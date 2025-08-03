package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.LocalPlayerAccess;
import net.minecraft.world.entity.Entity;

public class ShowRouterProcedure {
	public static boolean execute(Entity entity) {
		if (entity == null)
			return false;
		return entity instanceof LocalPlayerAccess playerAccess && playerAccess.administrator_authorization$isPressRouter();
	}
}
