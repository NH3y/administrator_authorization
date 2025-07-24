package net.mcreator.administratorauthorization.procedures;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;

import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;

import java.util.Objects;

public class DetectPermissionProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (entity.hasPermissions(2) && entity instanceof Player player) {
			if ((Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemstack.getItem())).toString()).equals("administrator_authorization:the_paper")) {
				ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.AUTHORIZER.get()).copy();
				_setstack.setCount(1);
				player.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				player.getInventory().setChanged();
			} else if ((Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemstack.getItem())).toString()).equals("administrator_authorization:the_sword")) {
				ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.REALITY_DESTROYER.get()).copy();
				_setstack.setCount(1);
				player.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				player.getInventory().setChanged();
			}
		} else {
			if ((Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemstack.getItem())).toString()).equals("administrator_authorization:authorizer")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.THE_PAPER.get()).copy();
					_setstack.setCount(1);
					_entity.setItemSlot(EquipmentSlot.MAINHAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			} else if ((Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(itemstack.getItem())).toString()).equals("administrator_authorization:reality_destroyer")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.THE_SWORD.get()).copy();
					_setstack.setCount(1);
					_entity.setItemSlot(EquipmentSlot.MAINHAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			}
		}
	}
}
