package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.Interfaces.LevelAccess;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgeLevel;
import net.minecraftforge.common.util.BlockSnapshot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

@Mixin(value = Level.class, priority = Integer.MIN_VALUE)
public abstract class LevelMixin implements LevelAccess, IForgeLevel, LevelAccessor {
    @Shadow
    public abstract @NotNull BlockState getBlockState(@NotNull BlockPos pPos);

    @Shadow
    @Final
    public boolean isClientSide;

    @Shadow
    @Final
    private boolean isDebug;

    @Shadow
    public abstract LevelChunk getChunkAt(BlockPos pPos);

    @Shadow
    public boolean captureBlockSnapshots;

    @Shadow
    public ArrayList<BlockSnapshot> capturedBlockSnapshots;

    @Shadow
    @Final
    private ResourceKey<Level> dimension;

    @Shadow
    public abstract void markAndNotifyBlock(BlockPos pPos, @Nullable LevelChunk levelchunk, BlockState blockstate, BlockState pState, int pFlags, int pRecursionLeft);

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    public void destroyBlock(BlockPos pPos, boolean pDropBlock, Entity pEntity, int pRecursionLeft, CallbackInfoReturnable<Boolean> cir) {
        BlockState blockState = this.getBlockState(pPos);
        if (blockState.getBlock() == AdministratorAuthorizationModBlocks.NOTHINGNESS.get()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "shouldTickDeath", at = @At("RETURN"), cancellable = true)
    public void shouldTickDeath(Entity pEntity, CallbackInfoReturnable<Boolean> cir) {
        if (pEntity instanceof EntityAccess access && access.administrator_authorization$getAuthorization()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getEntities(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/phys/AABB;Ljava/util/function/Predicate;)Ljava/util/List;", at = @At("RETURN"), cancellable = true)
    public void getEntities(Entity pEntity, AABB pBoundingBox, Predicate<? super Entity> pPredicate, CallbackInfoReturnable<List<Entity>> cir) {
        if (AAAuthorizationConfiguration.SPACE_INTERFERE.get()) {
            cir.setReturnValue(
                    cir.getReturnValue().stream().filter(entity ->
                                    !((EntityAccess) entity).administrator_authorization$getAuthorization())
                            .toList()
            );
        }
    }

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;II)Z", at = @At("HEAD"), cancellable = true)
    public void setBlock(BlockPos pPos, BlockState pState, int pFlags, int pRecursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (getBlockState(pPos).getBlock().getDescriptionId().equals("block.administrator_authorization.nothingness")) {
            cir.setReturnValue(false);
        }
    }

    @Override
    public boolean administrator_authorization$setBlock(BlockPos pPos, BlockState pState, int pFlags) {
        if (this.isOutsideBuildHeight(pPos)) {
            return false;
        } else if (!this.isClientSide && this.isDebug) {
            return false;
        } else {
            LevelChunk levelchunk = this.getChunkAt(pPos);
            pState.getBlock();

            pPos = pPos.immutable(); // Forge - prevent mutable BlockPos leaks
            net.minecraftforge.common.util.BlockSnapshot blockSnapshot = null;
            if (this.captureBlockSnapshots && !this.isClientSide) {
                blockSnapshot = net.minecraftforge.common.util.BlockSnapshot.create(this.dimension, this, pPos, pFlags);
                this.capturedBlockSnapshots.add(blockSnapshot);
            }

            BlockState old = getBlockState(pPos);
            old.getLightEmission(this, pPos);
            old.getLightBlock(this, pPos);

            BlockState blockstate = levelchunk.setBlockState(pPos, pState, (pFlags & 64) != 0);
            if (blockstate == null) {
                if (blockSnapshot != null) this.capturedBlockSnapshots.remove(blockSnapshot);
                return false;
            } else {
                BlockState blockstate1 = this.getBlockState(pPos);

                if (blockSnapshot == null) { // Don't notify clients or update physics while capturing blockstates
                    this.markAndNotifyBlock(pPos, levelchunk, blockstate, pState, pFlags, 512);
                }

                return true;
            }
        }
    }
}
