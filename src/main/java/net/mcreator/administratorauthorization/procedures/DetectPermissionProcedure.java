package net.mcreator.administratorauthorization.procedures;

import net.minecraftforge.registries.ForgeRegistries;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;

import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;

public class DetectPermissionProcedure {
	public static void execute(Entity entity, ItemStack itemstack) {
		if (entity == null)
			return;
		if (new Object() {
			int getPermissionLevel(Entity ent) {
				int lvl = 0;
				for (int Level = 0; Level < 4; Level++) {
					if (ent.hasPermissions(Level + 1)) {
						lvl = Level + 1;
					} else {
						break;
					}
				}
				return lvl;
			}
		}.getPermissionLevel(entity) >= 2) {
			if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals("administrator_authorization:the_paper")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.AUTHORIZER.get()).copy();
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			} else if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals("administrator_authorization:the_sword")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.REALITY_DESTROYER.get()).copy();
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			}
		} else {
			if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals("administrator_authorization:authorizer")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.THE_PAPER.get()).copy();
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			} else if ((ForgeRegistries.ITEMS.getKey(itemstack.getItem()).toString()).equals("administrator_authorization:reality_destroyer")) {
				if (entity instanceof LivingEntity _entity) {
					ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.THE_SWORD.get()).copy();
					_setstack.setCount(1);
					_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
					if (_entity instanceof Player _player)
						_player.getInventory().setChanged();
				}
			}
		}
	}
}
