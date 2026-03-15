package net.mcreator.administratorauthorization.EventTrackers;

import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.mcreator.administratorauthorization.procedures.DestroyRouterProcedure;
import net.minecraft.nbt.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Deprecated
@Mod.EventBusSubscriber
public class Gehenna {
    private static final CompoundTag tag = new CompoundTag();
    static {
        tag.put("Motion", newDoubleList(0, 0, 0));
        tag.put("Rotation", newFloatList(0, 0));
        tag.putFloat("FallDistance", 1000000.0F);
        tag.putShort("Fire", (short)65535);
        tag.putShort("Air", (short)0);
        tag.putBoolean("OnGround", false);
        tag.putBoolean("Invulnerable", false);
        tag.putInt("PortalCooldown", 10000000);
        tag.putInt("TicksFrozen", 10000000);
        tag.putBoolean("CanUpdate", false);
        tag.putFloat("Health", 0);
        tag.putShort("HurtTime", (short)0);
        tag.putInt("HurtByTimestamp", 0);
        tag.putShort("DeathTime", (short)20);
        tag.putFloat("AbsorptionAmount", 0);
        tag.putBoolean("FallFlying", false);
    }
    private static final List<LivingEntity> diabolos = new ArrayList<>();

    //@SubscribeEvent
    public static void atonement(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (Map.Entry<LivingEntity, Float> entity : DestroyRouterProcedure.cyclicVictim.entrySet()) {
                DestroyRouterProcedure.hellfire(entity.getKey(), event.player.level(), false, event.player);
            }
            DestroyRouterProcedure.cyclicVictim.replaceAll(((living, aFloat) -> aFloat - 1000));
            HashMap<LivingEntity, Float> checkMap = new HashMap<>(DestroyRouterProcedure.cyclicVictim);
            for (Map.Entry<LivingEntity, Float> entity : checkMap.entrySet()) {
                if (entity.getValue() <= 0) {
                    DestroyRouterProcedure.cyclicVictim.remove(entity.getKey());
                    if (entity.getKey().isAlive()) {
                        diabolos.add(entity.getKey());
                    }
                }
            }

            DestroyRouterProcedure.cyclicEntity.forEach(DestroyRouterProcedure::hellfire);
            DestroyRouterProcedure.cyclicEntity.clear();
        }
    }

    //@SubscribeEvent
    public static void atheos(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            for (LivingEntity entity : diabolos) {
                CompoundTag copied = tag.copy();
                copied.put("Pos", newDoubleList(entity.getX(), entity.getY(), entity.getZ()));
                copied.putUUID("UUID", entity.getUUID());
                copied.put("Attributes", ((AttributeAccess) entity.getAttributes()).administrator_authorization$clearSave());
                entity.load(copied);
            }
        }
    }

    private static ListTag newDoubleList(double... pNumbers) {
        ListTag listtag = new ListTag();

        for(double d0 : pNumbers) {
            listtag.add(DoubleTag.valueOf(d0));
        }

        return listtag;
    }

    private static ListTag newFloatList(float... pNumbers) {
        ListTag listtag = new ListTag();

        for(float f : pNumbers) {
            listtag.add(FloatTag.valueOf(f));
        }

        return listtag;
    }
}
