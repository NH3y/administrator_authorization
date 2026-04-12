package net.mcreator.administratorauthorization.mixins;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityDataAccessor.class, priority = Integer.MIN_VALUE)
public abstract class EntityDataAccessorsMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(int pId, EntityDataSerializer<?> pSerializer, CallbackInfo ci) throws ClassNotFoundException {
       //get name
    }

}
