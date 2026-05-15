package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.ServerLevelAccess;
import net.mcreator.administratorauthorization.network.HealthDataPacket;
import net.mcreator.administratorauthorization.network.OpenDataViewerPacket;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class DestroyRouterProcedure {
    private DestroyRouterProcedure() {}

    private static final ResourceLocation chaoticVoid = ResourceLocation.fromNamespaceAndPath(AdministratorAuthorizationMod.MODID, "chaotic_void");

    @SuppressWarnings("NonAsciiCharacters")
    public static void execute(Entity entity, Entity sourceentity, LevelAccessor world) {
        if (entity == null || sourceentity == null || entity.is(sourceentity))
            return;
        if (entity instanceof LivingEntity living) {
            int route = RouterDataOperant.getPlayerRouterIndex((Player) sourceentity);
            if (sourceentity instanceof PlayerAccess player && player.administrator_authorization$isPressAlter()) {
                switch (route) {
                    case 1 -> weaken(living);
                    case 2 -> disable(living);
                    case 3 -> neutralize(living);
                    case 4 -> DamnatioMemoriae(living, world);
                    default -> throw new IllegalArgumentException("Invalid route number");
                }
            } else {
                switch (route) {
                    case 1 -> damage(living, world);
                    case 2 -> kill(living, world);
                    case 3 -> defeat(living, world);
                    case 4 -> annihilate(living, world);
                    case 5 -> obliterate(living);
                    case 6 -> controller(living, sourceentity);
                    case 7 -> speedUp(living, world);
                    case 8 -> יוםהדין(living);
                    default -> throw new IllegalArgumentException("Invalid route number");
                }
            }
        }
    }

    private static void speedUp(LivingEntity living, LevelAccessor world) {
        try {
            Method tick = living.getClass().getDeclaredMethod("tick");
            for (int i = 0; i < 100; i++) {
                tick.invoke(living);
                damage(living, world);
            }
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private static void damage(LivingEntity victim, LevelAccessor world) {
        victim.hurt(new DamageSource(
                        world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ResourceKey.create(
                                        Registries.DAMAGE_TYPE,
                                        chaoticVoid)), victim.getKillCredit()),
                (float) 1024.0);
    }

    private static void kill(LivingEntity victim, LevelAccessor world) {
        victim.hurt(new DamageSource(
                        world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ResourceKey.create(
                                        Registries.DAMAGE_TYPE,
                                        chaoticVoid)), victim.getKillCredit()),
                Float.MAX_VALUE);
        restrictHealth(victim);
    }

    private static void defeat(LivingEntity victim, LevelAccessor world) {
        ((LivingEntityAccess) victim).administrator_authorization$setHealth(0.0F);
        restrictHealth(victim);
        victim.die(new DamageSource(
                world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ResourceKey.create(
                                Registries.DAMAGE_TYPE,
                                chaoticVoid)), victim.getKillCredit())
        );
    }

    private static void annihilate(LivingEntity victim, LevelAccessor world) {
        try {
            ((LivingEntityAccess) victim).administrator_authorization$accessDropLoot(world);
        } catch (Exception e) {
            victim.baseTick();
        }
        obliterate(victim);
    }

    private static void obliterate(LivingEntity victim) {
        if (victim instanceof EntityAccess access) {
            access.administrator_authorization$forceRemove();
        } else {
            victim.remove(Entity.RemovalReason.KILLED);
            victim.remove(Entity.RemovalReason.DISCARDED);
            victim.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            victim.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            victim.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
    }

    private static void controller(LivingEntity victim, Entity sourceentity) {
        if (sourceentity instanceof ServerPlayer serverPlayer) {
            PacketDistributor.sendToPlayer(serverPlayer, new OpenDataViewerPacket(victim));
        }
    }


    //Last Judgment
    @SuppressWarnings({"NonAsciiCharacters"})
    private static void יוםהדין(LivingEntity victim) {
        ((EntityAccess) victim).administrator_authorization$setRejectSave(true);
        obliterate(victim);
    }

    private static void weaken(LivingEntity victim) {
        if (victim instanceof LivingEntityAccess entity) {
            entity.administrator_authorization$setAttributes(Attributes.ATTACK_DAMAGE, 0);
            entity.administrator_authorization$setAttributes(Attributes.ARMOR_TOUGHNESS, 0);
            entity.administrator_authorization$setAttributes(Attributes.ARMOR, 0);
            entity.administrator_authorization$setAttributes(Attributes.MAX_HEALTH, victim.getAttributeBaseValue(Attributes.MAX_HEALTH));
            restrictHealth(victim);
        }
    }

    private static void disable(LivingEntity victim) {
        if (victim instanceof LivingEntityAccess entity) {
            entity.administrator_authorization$setAttributes(Attributes.MOVEMENT_SPEED, 0);
            entity.administrator_authorization$setAttributes(Attributes.FLYING_SPEED, 0);
        }
    }

    private static void neutralize(LivingEntity victim) {
        weaken(victim);
        disable(victim);
        ((LivingEntityAccess) victim).Administrator_authorization$setNoAI(true);
    }

    private static void DamnatioMemoriae(LivingEntity victim, LevelAccessor world) {
        if (world instanceof ServerLevelAccess serverLevel) {
            victim.setInvisible(true);
            victim.setPos(new Vec3(Integer.MAX_VALUE, -Integer.MAX_VALUE, Integer.MAX_VALUE));

            serverLevel.administrator_authorization$getEntityTickList().remove(victim);
            ((EntityAccess) victim).Administrator_authorization$setForgotten(true);
        }
    }

    private static void restrictHealth(LivingEntity victim) {
        HealthDataOperant.updateHealthLock(victim, true);
        HealthDataOperant.updateHealthLimit(victim, 0.0F);
        PacketDistributor
                .sendToServer(new HealthDataPacket(0.0F, true));
    }
}
