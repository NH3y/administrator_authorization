package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

import java.util.Map;

public interface AttributeAccess {
    void administrator_authorization$replaceValue(Holder<Attribute> attribute, double value);

    Map<Holder<Attribute>, AttributeInstance> administrator_authorization$getAllAttributes();
}
