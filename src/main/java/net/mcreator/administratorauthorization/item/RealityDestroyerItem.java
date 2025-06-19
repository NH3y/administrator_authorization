
package net.mcreator.administratorauthorization.item;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;

import net.mcreator.administratorauthorization.procedures.DetectPermissionProcedure;
import net.mcreator.administratorauthorization.procedures.DestroyRouterProcedure;

import com.google.common.collect.Multimap;
import com.google.common.collect.ImmutableMultimap;
import org.jetbrains.annotations.NotNull;

public class RealityDestroyerItem extends Item {
	public RealityDestroyerItem() {
		super(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
	}

	private boolean beHeld = false;

	@Override
	public @NotNull UseAnim getUseAnimation(@NotNull ItemStack itemstack) {
		return UseAnim.BLOCK;
	}

	@Override
	public float getDestroySpeed(@NotNull ItemStack par1ItemStack, @NotNull BlockState par2Block) {
		return 4f;
	}

	@Override
	public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot equipmentSlot) {
		if (equipmentSlot == EquipmentSlot.MAINHAND) {
			ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
			builder.putAll(super.getDefaultAttributeModifiers(equipmentSlot));
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Item modifier", 1023d, AttributeModifier.Operation.ADDITION));
			builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Item modifier", -2.4, AttributeModifier.Operation.ADDITION));
			return builder.build();
		}
		return super.getDefaultAttributeModifiers(equipmentSlot);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(@NotNull ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(@NotNull BlockState state) {
		return true;
	}

	@Override
	public boolean hurtEnemy(@NotNull ItemStack itemstack, @NotNull LivingEntity entity, @NotNull LivingEntity sourceentity) {
		boolean retval = super.hurtEnemy(itemstack, entity, sourceentity);
		DestroyRouterProcedure.execute(entity, sourceentity, sourceentity.level());
		return retval;
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if (selected){
			DetectPermissionProcedure.execute(entity, itemstack);
			this.beHeld = true;
		}else{
			this.beHeld = false;
		}
	}

    public boolean isBeHeld() {
        return beHeld;
    }
}
