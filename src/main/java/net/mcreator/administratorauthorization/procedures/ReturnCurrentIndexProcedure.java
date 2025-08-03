package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.minecraft.world.entity.Entity;

public class ReturnCurrentIndexProcedure {
	public static String execute(Entity entity) {
		if (entity == null)
			return "";
		int route = 0;
		if(entity instanceof PlayerAccess player) {
			route = player.administrator_authorization$isPressAlter() ? player.administrator_authorization$getRouter().getRouterAlter() : player.administrator_authorization$getRouter().getRouterMain();
		}
		return new java.text.DecimalFormat("##").format(route);
	}
}
