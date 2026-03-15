package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.PersistentEntitySectionManagerAccess;
import net.mcreator.administratorauthorization.Interfaces.ServerLevelAccess;
import net.mcreator.administratorauthorization.classes.TerminalClassFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityInLevelCallback;
import net.minecraft.world.level.entity.EntityTickList;
import net.minecraft.world.level.entity.LevelCallback;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BooleanSupplier;

@Mixin(ServerLevel.class)
public class ServerLevelMixin implements ServerLevelAccess {
    @Shadow
    @Final
    private PersistentEntitySectionManager<Entity> entityManager;

    @Shadow
    @Final
    EntityTickList entityTickList;

    @Unique
    private final TerminalClassFactory administrator_authorization$factory = TerminalClassFactory.getInstance();

    @Override
    public PersistentEntitySectionManager<Entity> administrator_authorization$getEntityManager() {
        return this.entityManager;
    }

    @Override
    public EntityTickList administrator_authorization$getEntityTickList() {
        return this.entityTickList;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(BooleanSupplier pHasTimeLeft, CallbackInfo ci) {
        this.entityTickList.forEach(entity -> {
            if (((EntityAccess) entity).administrator_authorization$isForgotten()) {
                entityTickList.remove(entity);
                LevelCallback<Entity> callback = (LevelCallback<Entity>) ((PersistentEntitySectionManagerAccess<?>) this.entityManager).administrator_authorization$getLevelCallbackBack();
                callback.onTrackingEnd(entity);
                callback.onDestroyed(entity);
                entity.setLevelCallback(EntityInLevelCallback.NULL);
                ((PersistentEntitySectionManagerAccess<? extends net.minecraft.world.level.entity.EntityAccess>) this.entityManager).administrator_authorization$removeUuid(entity);
            }
        });
    }

    @Inject(method = "broadcastEntityEvent", at = @At("HEAD"), cancellable = true)
    public void broadcastEntityEvent(Entity pEntity, byte pState, CallbackInfo ci) {
        if (pState == 3 && ((EntityAccess) pEntity).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @Inject(method = "broadcastDamageEvent", at = @At("HEAD"), cancellable = true)
    public void broadcastDamageEvent(Entity pEntity, DamageSource pDamageSource, CallbackInfo ci) {
        if (((EntityAccess) pEntity).administrator_authorization$getAuthorization()) {
            ci.cancel();
        }
    }

    @ModifyVariable(method = "addFreshEntity", at = @At("HEAD"), argsOnly = true, index = 1)
    public Entity addFreshEntity(Entity value) {
        if (administrator_authorization$factory.hasProxyFor(value.getClass())) {
            Entity proxy = administrator_authorization$factory.createProxy(value, (ServerLevel) (Object) this);
            return proxy == null ? value : proxy;
        }
        return value;
    }

    @ModifyVariable(method = "tryAddFreshEntityWithPassengers", at = @At("HEAD"), argsOnly = true, index = 1)
    public Entity addFreshEntityWithPassengers(Entity value) {
        if (administrator_authorization$factory.hasProxyFor(value.getClass())) {
            Entity proxy = administrator_authorization$factory.createProxy(value, (ServerLevel) (Object) this);
            return proxy == null ? value : proxy;
        }
        return value;
    }

    @ModifyVariable(method = "addWithUUID", at = @At("HEAD"), argsOnly = true, index = 1)
    public Entity addWithUUID(Entity value) {
        if (administrator_authorization$factory.hasProxyFor(value.getClass())) {
            Entity proxy = administrator_authorization$factory.createProxy(value, (ServerLevel) (Object) this);
            return proxy == null ? value : proxy;
        }
        return value;
    }
}
