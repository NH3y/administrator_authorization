package net.mcreator.administratorauthorization.mixins;

import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TransientEntitySectionManager.class)
public class TransientEntitySectionManagerMixin<T> implements net.mcreator.administratorauthorization.Interfaces.TransientEntitySectionManagerAccess<T> {
    @Shadow
    @Final
    LevelCallback<T> callbacks;

    @Override
    public LevelCallback<T> administrator_authorization$getLevelCallbackBack(){
        return this.callbacks;
    }
}
