package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.entity.Entity;

public class CallAuthoritySwitchProcedure {
	public static void execute(Entity entity) {
		if(entity instanceof EntityAccess data){
			data.administrator_authorization$setSwitch(!data.administrator_authorization$getSwitch());
		}
	}
}
