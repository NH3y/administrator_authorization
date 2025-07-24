package net.mcreator.administratorauthorization.mixins;

//import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@OnlyIn(Dist.CLIENT)
@Mixin(value = ItemCooldowns.class,priority = Integer.MAX_VALUE)
public class ItemCooldownMixin {
    @Inject(method = "addCooldown", at = @At("HEAD"), cancellable = true)
    public void addCooldown(Item pItem, int pTicks, CallbackInfo ci){
        if(pItem.getDefaultInstance().is(AdministratorAuthorizationModItems.REALITY_DESTROYER.get())){
            ci.cancel();
            //AdministratorAuthorizationMod.LOGGER.info("Mixin : Cooldown");
        }
    }
}
