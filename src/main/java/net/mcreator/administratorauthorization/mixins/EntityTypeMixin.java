package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.classes.TerminalClassFactory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Function;

@Mixin(EntityType.class)
public class EntityTypeMixin<T extends Entity> {
    @Unique
    private static final TerminalClassFactory administrator_authorization$factory = TerminalClassFactory.getInstance();

    @Inject(method = "loadEntityRecursive", at = @At("RETURN"), cancellable = true)
    private static void loadEntityRecursive(CompoundTag pCompound, Level pLevel, Function<Entity, Entity> pEntityFunction, CallbackInfoReturnable<Entity> cir) {
        Entity original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        if (administrator_authorization$factory.hasProxyFor(original.getClass()) && pLevel instanceof ServerLevel serverLevel) {
            Entity proxy = administrator_authorization$factory.createProxy(original, serverLevel);
            if (proxy != null) {
                cir.setReturnValue(proxy);
            }
        }
    }

    @Inject(method = "create(Lnet/minecraft/world/level/Level;)Lnet/minecraft/world/entity/Entity;", at = @At("RETURN"), cancellable = true)
    public void create(Level pLevel, CallbackInfoReturnable<T> cir) {
        T original = cir.getReturnValue();
        if (original == null) {
            return;
        }
        if (administrator_authorization$factory.hasProxyFor(original.getClass()) && pLevel instanceof ServerLevel serverLevel) {
            T proxy = administrator_authorization$factory.createProxy(original, serverLevel);
            if (proxy != null) {
                cir.setReturnValue(proxy);
            }
        }
    }
}
