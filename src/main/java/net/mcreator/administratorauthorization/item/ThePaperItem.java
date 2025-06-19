
package net.mcreator.administratorauthorization.item;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.Entity;

import net.mcreator.administratorauthorization.procedures.DetectPermissionProcedure;
import org.jetbrains.annotations.NotNull;

public class ThePaperItem extends Item {
	public ThePaperItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.COMMON));
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected)
			DetectPermissionProcedure.execute(entity, itemstack);
	}
}
