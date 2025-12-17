
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.block.NothingnessBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AdministratorAuthorizationModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(Registries.BLOCK, AdministratorAuthorizationMod.MODID);
    public static final DeferredHolder<Block, NothingnessBlock> NOTHINGNESS = REGISTRY.register("nothingness", NothingnessBlock::new);
    // Start of user code block custom blocks
    // End of user code block custom blocks
}
