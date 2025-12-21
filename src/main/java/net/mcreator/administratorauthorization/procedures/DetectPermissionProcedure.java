package net.mcreator.administratorauthorization.procedures;

import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DetectPermissionProcedure {
    public static void execute(Entity entity, ItemStack itemstack) {
        if (entity == null)
            return;
        if (entity.hasPermissions(AAAuthorizationConfiguration.REQUIRED_LEVEL.get()) && entity instanceof Player player) {
            if (itemstack.is(AdministratorAuthorizationModItems.THE_PAPER)) {
                ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.AUTHORIZER.get()).copy();
                _setstack.setCount(1);
                player.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                player.getInventory().setChanged();
            } else if (itemstack.is(AdministratorAuthorizationModItems.THE_SWORD)) {
                ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.REALITY_DESTROYER.get()).copy();
                _setstack.setCount(1);
                player.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
                player.getInventory().setChanged();
            }
        } else {
            if (itemstack.is(AdministratorAuthorizationModItems.AUTHORIZER)) {
                if (entity instanceof LivingEntity _entity) {
                    ItemStack _setstack = new ItemStack(AdministratorAuthorizationModItems.THE_PAPER.get()).copy();
                    _setstack.setCount(1);
                    _entity.setItemSlot(EquipmentSlot.MAINHAND, _setstack);
                    if (_entity instanceof Player _player)
                        _player.getInventory().setChanged();
                }
            } else if (itemstack.is(AdministratorAuthorizationModItems.REALITY_DESTROYER)) {
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
