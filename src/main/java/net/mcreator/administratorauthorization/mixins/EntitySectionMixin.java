package net.mcreator.administratorauthorization.mixins;

import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.EntitySection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;

@Mixin(EntitySection.class)
public class EntitySectionMixin<T extends EntityAccess> {
    @Inject(method = "getEntities()Ljava/util/stream/Stream;", at = @At("RETURN"), cancellable = true)
    public void missingEntity(CallbackInfoReturnable<Stream<T>> cir) {
        cir.setReturnValue(
                cir.getReturnValue().filter(entity -> !((net.mcreator.administratorauthorization.Interfaces.EntityAccess) entity).administrator_authorization$isRejectSave())
        );
    }
}
