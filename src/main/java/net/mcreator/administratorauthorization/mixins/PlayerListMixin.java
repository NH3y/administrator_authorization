package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.errors.AdminDeathException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "respawn", at = @At("HEAD"), cancellable = true)
    public void respawn(ServerPlayer player, boolean keepInventory, Entity.RemovalReason reason, CallbackInfoReturnable<ServerPlayer> cir) {
        if (player instanceof EntityAccess entity && entity.administrator_authorization$getAuthorization()) {
            if (!keepInventory) {
                cir.setReturnValue(player);
                AdministratorAuthorizationMod.LOGGER.info("Mixin : Respawn Fail");
                throw new AdminDeathException("");
            }
        }
    }
}
