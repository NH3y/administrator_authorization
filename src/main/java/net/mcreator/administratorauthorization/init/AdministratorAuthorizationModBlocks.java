
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.block.NothingnessBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AdministratorAuthorizationModBlocks {
    public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, AdministratorAuthorizationMod.MODID);
    public static final RegistryObject<Block> NOTHINGNESS = REGISTRY.register("nothingness", NothingnessBlock::new);
    // Start of user code block custom blocks
    // End of user code block custom blocks
}
