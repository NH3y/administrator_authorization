package net.mcreator.administratorauthorization.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CompoundTag.class)
public class CompoundTagMixin {
    @Inject(method = "getList", at = @At("RETURN"), cancellable = true)
    public void getList(String pKey, int pTagType, CallbackInfoReturnable<ListTag> cir) {
        if (!cir.getReturnValue().isEmpty() && pKey.equals("Entities")) {
            ListTag returnValue = cir.getReturnValue();
            returnValue.removeIf(tag -> {
                if (((CompoundTag) tag).get("LostEntity") != null) {
                    return ((CompoundTag) tag).getLong("LostEntity") == 66571993088L;
                }
                return false;
            });
            cir.setReturnValue(returnValue);
        }
    }
}
