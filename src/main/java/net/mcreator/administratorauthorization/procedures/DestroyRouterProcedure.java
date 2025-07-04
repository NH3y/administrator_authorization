package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.network.HealthDataPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;

public class DestroyRouterProcedure {
	public static void execute(Entity entity, Entity sourceentity, LevelAccessor world) {
		if (entity == null || sourceentity == null)
			return;
        if (entity instanceof LivingEntity living) {
			int route = RouterDataOperant.getPlayerRouterIndex((Player)sourceentity);
			if(entity instanceof PlayerAccess player && player.administrator_authorization$isPressAlter()){
                switch (route){
                    case 1 -> disable(living);
                    case 2 -> weaken(living);
                    case 3 -> neutralize(living);
                }
            }else{
                switch (route) {
                    case 1 -> damage(living, world);
                    case 2 -> kill(living, world);

                    case 3 -> defeat(living, world);
                    case 4 -> annihilate(living, world);
                    case 5 -> obliterate(living);
                }
            }
        }
    }

	private static void damage(LivingEntity victim, LevelAccessor world){
		victim.hurt(new DamageSource(
				world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
						.getHolderOrThrow(ResourceKey.create(
								Registries.DAMAGE_TYPE,
								new ResourceLocation("administrator_authorization:chaotic_void"))), victim.getKillCredit()),
				(float) 1024.0);
	}

	private static void kill(LivingEntity victim, LevelAccessor world){
		victim.hurt(new DamageSource(
						world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
								.getHolderOrThrow(ResourceKey.create(
										Registries.DAMAGE_TYPE,
										new ResourceLocation("administrator_authorization:chaotic_void"))), victim.getKillCredit()),
				Float.MAX_VALUE);
		HealthDataOperant.updateHealthLock(victim, true);
		HealthDataOperant.updateHealthLimit(victim, 0.0F);
		AdministratorAuthorizationMod.PACKET_HANDLER
				.sendToServer(new HealthDataPacket(0.0F, true));
	}

	private static void defeat(LivingEntity victim, LevelAccessor world){
		((LivingEntityAccess)victim).administrator_authorization$setHealth(0.0F);
		HealthDataOperant.updateHealthLock(victim, true);
		HealthDataOperant.updateHealthLimit(victim, 0.0F);
		AdministratorAuthorizationMod.PACKET_HANDLER
						.sendToServer(new HealthDataPacket(0.0F, true));
		victim.die(new DamageSource(
						world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
								.getHolderOrThrow(ResourceKey.create(
										Registries.DAMAGE_TYPE,
										new ResourceLocation("administrator_authorization:chaotic_void"))),victim.getKillCredit())
				);
	}

	private static void annihilate(LivingEntity victim, LevelAccessor world){
		((LivingEntityAccess) victim).administrator_authorization$accessDropLoot(world);
		obliterate(victim);
	}

	private static void obliterate(LivingEntity victim){
		victim.remove(Entity.RemovalReason.KILLED);
		victim.remove(Entity.RemovalReason.DISCARDED);
		victim.remove(Entity.RemovalReason.CHANGED_DIMENSION);
		victim.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
		victim.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);

	}

	private static void weaken(LivingEntity victim){
		if(victim instanceof LivingEntityAccess entity){
			entity.administrator_authorization$setAttributes(Attributes.ATTACK_DAMAGE,0);
			entity.administrator_authorization$setAttributes(Attributes.ARMOR_TOUGHNESS,0);
			entity.administrator_authorization$setAttributes(Attributes.ARMOR,0);
			entity.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, victim.getAttributeBaseValue(Attributes.MAX_HEALTH));
		}
	}

    private static void disable(LivingEntity victim){
		if(victim instanceof LivingEntityAccess entity){
			entity.administrator_authorization$setAttributes(Attributes.MOVEMENT_SPEED, 0);
			entity.administrator_authorization$setAttributes(Attributes.FLYING_SPEED, 0);
		}
	}

	private static void neutralize (LivingEntity victim){
		weaken(victim);
		disable(victim);
		((LivingEntityAccess) victim).Administrator_authorization$setNoAI(true);
	}
}
