package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Level.class, priority = Integer.MAX_VALUE)
public abstract class LevelMixin implements net.mcreator.administratorauthorization.Interfaces.LevelAccess {
    @Shadow public abstract BlockState getBlockState(BlockPos pPos);

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    public void destroyBlock(BlockPos pPos, boolean pDropBlock, Entity pEntity, int pRecursionLeft, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.getBlockState(pPos);
        if(blockState.getBlock() == AdministratorAuthorizationModBlocks.NOTHINGNESS.get()){
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldTickDeath", at = @At("RETURN"), cancellable = true)
    public void shouldTickDeath(Entity pEntity, CallbackInfoReturnable<Boolean> cir){
        if(pEntity instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean administrator_authorization$destroyBlock(){
        return true;
    }
}
