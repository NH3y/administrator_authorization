package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;

public class AuthorizePlayerProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		if (new Object() {
			int getPermissionLevel(Entity ent) {
				int lvl = 0;
				for (int Level = 0; Level < 4; Level++) {
					if (ent.hasPermissions(Level + 1)) {
						lvl = Level + 1;
					} else {
						break;
					}
				}
				return lvl;
			}
		}.getPermissionLevel(entity) >= 2) {
			if (entity instanceof Player){
				((EntityAccess) entity).administrator_authorization$setAuthorization();
			}

		}
	}
}
