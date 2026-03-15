package net.mcreator.administratorauthorization.mixins;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityTickList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@Mixin(EntityTickList.class)
public class EntityTickListMixin {
    @Shadow
    private Int2ObjectMap<Entity> active;

    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void add(Entity pEntity, CallbackInfo ci) {
        if (((EntityAccess) pEntity).administrator_authorization$isFreeze()) {
            ci.cancel();
        }
    }

    @Inject(method = "forEach", at = @At(value = "FIELD", target = "Lnet/minecraft/world/level/entity/EntityTickList;iterated:Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;",opcode = Opcodes.PUTFIELD))
    public void accept(Consumer<Entity> pEntity, CallbackInfo ci) {
        Set<Integer> ids = new HashSet<>();
        for (Int2ObjectMap.Entry<Entity> entityEntry : this.active.int2ObjectEntrySet()) {
            Entity entity = entityEntry.getValue();
            if (((EntityAccess) entity).administrator_authorization$isFreeze()) {
                ids.add(entityEntry.getIntKey());
            }
        }
        ids.forEach(id -> active.remove(id.intValue()));
    }
}

