package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.commands.data.EntityDataAccessor;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityDataAccessor.class)
public class EntityDataAccessorMixin {
    @Shadow @Final private Entity entity;
    @Unique
    private boolean administrator_authorization$protect;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(Entity pEntity, CallbackInfo ci){
        this.administrator_authorization$protect = ((EntityAccess) this.entity).administrator_authorization$getAuthorization();
    }

    @Inject(method = "getData", at = @At("RETURN"), cancellable = true)
    public void getData(CallbackInfoReturnable<CompoundTag> cir){
        if(this.administrator_authorization$protect){
            CompoundTag tag = cir.getReturnValue();
            tag.putFloat("Health", ((LivingEntityAccess) this.entity).administrator_authorization$getFixedMaxHealth());
            tag.putShort("DeathTime", (short) 0);
            cir.setReturnValue(tag);
        }
    }
}
