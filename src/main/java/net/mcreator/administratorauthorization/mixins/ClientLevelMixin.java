package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.TransientEntitySectionManagerAccess;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.TransientEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @Shadow
    @Final
    EntityTickList tickingEntities;

    @Shadow
    @Final
    private TransientEntitySectionManager<Entity> entityStorage;

    @Inject(method = "tickEntities", at = @At("HEAD"))
    public void tick(CallbackInfo ci){
        this.tickingEntities.forEach(entity -> {
            if(((EntityAccess) entity).Administrator_authorization$isForgotten()){
                this.tickingEntities.remove(entity);
                LevelCallback<Entity> callback = (LevelCallback<Entity>) ((TransientEntitySectionManagerAccess<?>) this.entityStorage).administrator_authorization$getLevelCallbackBack();
                callback.onTrackingEnd(entity);
                entity.setLevelCallback(EntityInLevelCallback.NULL);
            }
        });
    }
}
