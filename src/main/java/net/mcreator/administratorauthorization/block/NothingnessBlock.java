
package net.mcreator.administratorauthorization.block;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class NothingnessBlock extends Block {
	public NothingnessBlock() {
		super(BlockBehaviour.Properties.of().mapColor(MapColor.NONE).sound(SoundType.GRAVEL).strength(-1, Float.MAX_VALUE).pushReaction(PushReaction.BLOCK));
	}

	@Override
	public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
		return true;
	}

	@Override
	public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos pos) {
		return 0;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		return new ItemStack(Blocks.AIR);
	}

	@Override
	public void destroy(@NotNull LevelAccessor pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState) {
		pLevel.setBlock(pPos, pState, 48);
	}

	@Override
	protected void spawnDestroyParticles(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull BlockPos pPos, @NotNull BlockState pState) {}

	@Override
	public boolean canEntityDestroy(BlockState state, BlockGetter level, BlockPos pos, Entity entity) {
		return false;
	}

	@Override
	public void playerWillDestroy(@NotNull Level pLevel, @NotNull BlockPos pPos, @NotNull BlockState pState, @NotNull Player pPlayer) {
	}
}
