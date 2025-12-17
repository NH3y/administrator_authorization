
package net.mcreator.administratorauthorization.item;

import com.google.common.collect.ImmutableMultimap;
import net.mcreator.administratorauthorization.procedures.DetectPermissionProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class TheSwordItem extends Item {
    private final ItemAttributeModifiers attributeModifiers;

    public TheSwordItem() {
        super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
        ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
        builder.add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 7d, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        builder.add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND);
        this.attributeModifiers = builder.build();
    }

    @Override
    public @NotNull ItemAttributeModifiers getDefaultAttributeModifiers(@NotNull ItemStack stack) {
        return this.attributeModifiers;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(itemstack, world, entity, slot, selected);
        if (selected)
            DetectPermissionProcedure.execute(entity, itemstack);
    }
}
