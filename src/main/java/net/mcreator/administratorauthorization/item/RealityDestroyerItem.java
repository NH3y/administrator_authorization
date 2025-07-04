
package net.mcreator.administratorauthorization.item;

import net.mcreator.administratorauthorization.Interfaces.PlayerAccess;
import net.mcreator.administratorauthorization.configuration.AADestroyerConfiguration;
import net.mcreator.administratorauthorization.procedures.SearchForEntityProcedure;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
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

import java.util.ArrayList;

public class RealityDestroyerItem extends Item {
	public RealityDestroyerItem() {
		super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC));
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
		if(AADestroyerConfiguration.RANGED.get()){
			double range = AADestroyerConfiguration.RADIUS.get();
			AABB aabb = new AABB(entity.position().subtract(range,0, range), entity.getEyePosition().subtract(-range, -1, -range));
			ArrayList<Entity> targets = (ArrayList<Entity>) entity.level().getEntities(entity, aabb);
			for(Entity victim : targets){
				DestroyRouterProcedure.execute(victim, sourceentity, sourceentity.level());
			}
		}else {
			DestroyRouterProcedure.execute(entity, sourceentity, sourceentity.level());
		}
		return retval;
	}

	@Override
	public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level world, @NotNull Player entity, @NotNull InteractionHand hand) {
		InteractionResultHolder<ItemStack> ar = super.use(world, entity, hand);
		ArrayList<Entity> target = SearchForEntityProcedure.execute(world, entity);
		if (!target.isEmpty() && !world.isClientSide()) {
			if(AADestroyerConfiguration.ACCEPT_MULTIPLE.get()) {
				target.forEach(victim -> DestroyRouterProcedure.execute(victim, entity, world));
			}else{
				DestroyRouterProcedure.execute(target.get(0), entity, world);
			}
		}
		return ar;
	}

	@Override
	public void inventoryTick(@NotNull ItemStack itemstack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
		super.inventoryTick(itemstack, world, entity, slot, selected);
		if(AADestroyerConfiguration.KEEP_IN_INVENTORY.get()){
			int old = ((PlayerAccess) entity).administrator_authorization$getRDSlot();
			if(old != Integer.MAX_VALUE && old != slot) {
				((Player)entity).getInventory().removeItem(old, 1);
			}
			((PlayerAccess) entity).administrator_authorization$setRDSlot(slot);
		}
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
