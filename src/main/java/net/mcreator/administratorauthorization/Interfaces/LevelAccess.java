package net.mcreator.administratorauthorization.Interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public interface LevelAccess {
    boolean administrator_authorization$setBlock(BlockPos pPos, BlockState pNewState, int pFlags);


}
