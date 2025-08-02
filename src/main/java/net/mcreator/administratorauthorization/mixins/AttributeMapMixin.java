package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
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

@Mixin(value = AttributeMap.class,priority = Integer.MAX_VALUE)
public abstract class AttributeMapMixin implements AttributeAccess {
    @Shadow @Final private Map<Attribute, AttributeInstance> attributes;

    @Shadow @Nullable public abstract AttributeInstance getInstance(Attribute pAttribute);

    @Shadow public abstract ListTag save();

    @Override
    public void administrator_authorization$replaceValue(Attribute attribute, double value){
        AttributeInstance instance = this.getInstance(attribute);
        if (instance != null) {
            this.attributes.replace(attribute,new AttributeMap(AttributeSupplier.builder().add(attribute,value).build()).getInstance(attribute));
            this.save();
        }
    }

    @Override
    public Map<Attribute, AttributeInstance> administrator_authorization$getAllAttributes(){
        return this.attributes;
    }
}
