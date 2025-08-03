package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.level.LevelAccessor;

public interface LivingEntityAccess {

    void administrator_authorization$accessDropLoot(LevelAccessor world);

    void Administrator_authorization$setNoAI(boolean administrator_authorization$NoAI);

    void administrator_authorization$setAttributes(Attribute attribute, double value);

    float administrator_authorization$getFixedMaxHealth();

    void administrator_authorization$setHealth(float value);

    EntityDataAccessor<Float> administrator_authorization$getAccessorHealth();
}
