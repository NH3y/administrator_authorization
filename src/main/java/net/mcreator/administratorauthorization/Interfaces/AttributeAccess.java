package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Map;

public interface AttributeAccess {
    void administrator_authorization$replaceValue(Attribute attribute, double value);

    Map<Attribute, AttributeInstance> administrator_authorization$getAllAttributes();
}
