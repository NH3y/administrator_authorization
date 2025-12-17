package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.minecraft.core.Holder;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import javax.annotation.Nullable;
import java.util.Map;

@Mixin(value = AttributeMap.class, priority = Integer.MIN_VALUE)
public abstract class AttributeMapMixin implements AttributeAccess {
    @Shadow
    @Final
    private Map<Holder<Attribute>, AttributeInstance> attributes;

    @Shadow
    public abstract ListTag save();

    @Shadow
    @Nullable
    public abstract AttributeInstance getInstance(Holder<Attribute> attribute);

    @Override
    public void administrator_authorization$replaceValue(Holder<Attribute> attribute, double value) {
        AttributeInstance instance = this.getInstance(attribute);
        if (instance != null) {
            this.attributes.replace(attribute, new AttributeMap(AttributeSupplier.builder().add(attribute, value).build()).getInstance(attribute));
            this.save();
        }
    }

    @Override
    public Map<Holder<Attribute>, AttributeInstance> administrator_authorization$getAllAttributes() {
        return this.attributes;
    }
}
