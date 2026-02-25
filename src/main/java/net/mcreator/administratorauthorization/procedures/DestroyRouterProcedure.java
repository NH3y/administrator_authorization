package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.Interfaces.ServerLevelAccess;
import net.mcreator.administratorauthorization.classes.Vault;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class DestroyRouterProcedure {
    public static final Map<LivingEntity, Float> cyclicVictim = new HashMap<>();
    public static final List<Entity> cyclicEntity = new ArrayList<>();

    @SuppressWarnings("NonAsciiCharacters")
    public static void execute(Entity entity, Entity sourceentity, LevelAccessor world) {
        if (entity == null || sourceentity == null || entity.is(sourceentity))
            return;
        int route = RouterDataOperant.getPlayerRouterIndex((Player) sourceentity);
        if (entity instanceof LivingEntity living) {
            if (sourceentity instanceof PlayerAccess player && player.administrator_authorization$isPressAlter()) {
                switch (route) {
                    case 1 -> weaken(living);
                    case 2 -> disable(living);
                    case 3 -> neutralize(living);
                    case 4 -> DamnatioMemoriae(living, world);

                }
            } else {
                switch (route) {
                    case 1 -> damage(living, world, 1024, sourceentity);
                    case 2 -> kill(living, world, sourceentity);
                    case 3 -> defeat(living, world, sourceentity);
                    case 4 -> annihilate(living, world);
                    case 5 -> obliterate(living);
                    case 6 -> disintegrate(living);
                    case 7 -> selfDestruct(living, world);
                    case 8 -> יוםהדין(living);
                }
            }
        }else if (shouldKill(entity)) {
            switch (route) {
                case 1, 2, 3, 4 -> {}
                case 5 -> obliterate(entity);
                case 8 -> יוםהדין(entity);
            }
        }
    }

    private static boolean shouldKill(Entity entity) {
        return entity instanceof Display || AADestroyerConfiguration.ACCEPT_ENTITY.get();
    }

    private static void damage(LivingEntity victim, LevelAccessor world, float damage, Entity sourceentity) {
        if (sourceentity instanceof Player player) {
            player.attack(victim);
        }
        victim.hurt(new DamageSource(
                        world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ResourceKey.create(
                                        Registries.DAMAGE_TYPE,
                                        new ResourceLocation("administrator_authorization:chaotic_void"))), sourceentity),
                damage);
        tracking();
    }

    private static void kill(LivingEntity victim, LevelAccessor world, Entity sourceentity) {
        damage(victim, world, Float.MAX_VALUE, sourceentity);
        restrictHealth(victim);
        tracking();
    }

    private static void defeat(LivingEntity victim, LevelAccessor world, Entity sourceentity) {
        ((LivingEntityAccess) victim).administrator_authorization$setHealth(0.0F);
        restrictHealth(victim);
        victim.die(new DamageSource(
                world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ResourceKey.create(
                                Registries.DAMAGE_TYPE,
                                new ResourceLocation("administrator_authorization:chaotic_void"))), sourceentity)
        );
        tracking();
    }

    private static void annihilate(LivingEntity victim, LevelAccessor world) {
        try {
            ((LivingEntityAccess) victim).administrator_authorization$accessDropLoot(world);
        } catch (Exception e) {
            victim.baseTick();
        }
        obliterate(victim);
    }

    private static void obliterate(Entity victim) {
        if (victim instanceof EntityAccess access) {
            access.administrator_authorization$forceRemove();
        } else {
            victim.remove(Entity.RemovalReason.KILLED);
            victim.remove(Entity.RemovalReason.DISCARDED);
            victim.remove(Entity.RemovalReason.CHANGED_DIMENSION);
            victim.remove(Entity.RemovalReason.UNLOADED_TO_CHUNK);
            victim.remove(Entity.RemovalReason.UNLOADED_WITH_PLAYER);
        }
        tracking();
    }

    public static void disintegrate(LivingEntity victim) {
        if (cyclicVictim.keySet().stream().noneMatch(cyclicEntity -> cyclicEntity.getId() == victim.getId())) {
            cyclicVictim.put(victim, victim.getMaxHealth());
            tracking();
        }
    }

    public static void selfDestruct(LivingEntity victim,  LevelAccessor world) {
        Vault.damageMethods.getData().forEach(method -> {
            try {
                method.invoke(victim, new DamageSource(
                        world.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE)
                                .getHolderOrThrow(ResourceKey.create(
                                        Registries.DAMAGE_TYPE,
                                        new ResourceLocation("administrator_authorization:chaotic_void"))), victim.getKillCredit()),
                        Float.MAX_VALUE - 1);
            } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException ignore) {
            }
        });
    }


    //Last Judgment
    @SuppressWarnings({"NonAsciiCharacters"})
    private static void יוםהדין(Entity victim) {
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
    }

    private static void tracking() {
        List<String> collect = Arrays.stream(Thread.currentThread().getStackTrace())
                .filter(DestroyRouterProcedure::suspicious)
                .map(stackTraceElement -> {
                    String[] split = stackTraceElement.getClassName().split("([.\\\\])");
                    return split[split.length - 1] + ":" + stackTraceElement.getMethodName();
                }).toList();
        if (!collect.isEmpty()) {
            System.out.println(collect);
        }
    }

    private static boolean suspicious(StackTraceElement element) {
        String className = element.getClassName();
        return !className.startsWith("net.minecraft") && !className.startsWith("net.mcreator.administratorauthorization") &&
                !className.startsWith("java.lang") && !className.startsWith("java.util");
    }

    public static void hellfire(LivingEntity victim, LevelAccessor world, boolean force, Player player) {
        if (!force) {
            damage(victim, world, 1024, player);
        } else {
            obliterate(victim);
        }
        neutralize(victim);
    }

    public static void hellfire(Entity victim) {
        obliterate(victim);
    }
}
