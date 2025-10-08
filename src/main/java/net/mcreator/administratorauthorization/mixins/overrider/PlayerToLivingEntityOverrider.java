package net.mcreator.administratorauthorization.mixins.overrider;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LivingEntityAccess;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = Player.class, priority = Integer.MAX_VALUE)
public abstract class PlayerToLivingEntityOverrider extends LivingEntity {
    protected PlayerToLivingEntityOverrider(EntityType<? extends LivingEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Override
    public float getHealth() {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            return Math.max(this.entityData.get(((LivingEntityAccess) this).administrator_authorization$getAccessorHealth()), 20f);
        }
        return super.getHealth();
    }

    @Override
    public boolean isDeadOrDying() {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            return false;
        }
        return super.isDeadOrDying();
    }

    @Override
    public boolean isAlive() {
        if (((EntityAccess) this).administrator_authorization$getAuthorization()) {
            return true;
        }
        return super.isAlive();
    }
}
