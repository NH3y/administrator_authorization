package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public class SearchForEntityProcedure {
	public static ArrayList<Entity> execute(LevelAccessor world, Entity source){
		final ArrayList<Entity> targets = new ArrayList<>();
		AABB aabb = new AABB(source.getEyePosition(), source.getEyePosition()).inflate(0.5);
		for (int i = 0; i < AADestroyerConfiguration.SEARCH_DISTANCE.get(); i++) {
			targets.addAll(world.getEntities(EntityTypeTest.forClass(Entity.class), aabb.move(source.getLookAngle().scale(i)), entity ->
					entity != null && !entity.is(source)
			));
			if(world instanceof ServerLevel level){
				level.sendParticles(ParticleTypes.ASH,
						source.getX() + i *source.getLookAngle().x(),
						source.getEyeY() + i *source.getLookAngle().y(),
						source.getY() + i *source.getLookAngle().z(),
						50,
						0,
						0,
						0,
						0.1
						);
			}else{
				world.addParticle(ParticleTypes.END_ROD,
						source.getX() + i *source.getLookAngle().x(),
						source.getEyeY() + i *source.getLookAngle().y(),
						source.getY() + i *source.getLookAngle().z(),
						0,
						0,
						0
						);
			}
			if(!targets.isEmpty() && !AADestroyerConfiguration.ACCEPT_MULTIPLE.get()){
				break;
			}
		}
		return targets;
	}
}
