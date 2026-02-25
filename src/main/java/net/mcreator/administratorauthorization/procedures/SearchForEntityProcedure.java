package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;

public class SearchForEntityProcedure {
    public static ArrayList<Entity> execute(Level world, Entity source) {
        final ArrayList<Entity> targets = new ArrayList<>();
        AABB aabb = new AABB(source.getEyePosition(), source.getEyePosition()).inflate(0.5);
        for (int i = 0; i < AADestroyerConfiguration.SEARCH_DISTANCE.get(); i++) {
            if (world instanceof ServerLevel level) {
                int finalI = i;
                level.getEntities().getAll().forEach(entity -> {
                    if (!entity.is(source) && entity.getBoundingBox().intersects(aabb.move(source.getLookAngle().scale(finalI)))) {
                        targets.add(entity);
                    }
                });

                level.sendParticles(ParticleTypes.ASH,
                        source.getX() + i * source.getLookAngle().x(),
                        source.getEyeY() + i * source.getLookAngle().y(),
                        source.getZ() + i * source.getLookAngle().z(),
                        25,
                        0,
                        0,
                        0,
                        0.1
                );
            } else if (world instanceof ClientLevel level) {
                level.getEntities(EntityTypeTest.forClass(Entity.class), aabb.move(source.getLookAngle().scale(i)),
                        entity -> entity != null && !entity.is(source)
                );
            }
            if (!targets.isEmpty() && !AADestroyerConfiguration.ACCEPT_MULTIPLE.get()) {
                break;
            }
        }
        return targets;
    }
}
