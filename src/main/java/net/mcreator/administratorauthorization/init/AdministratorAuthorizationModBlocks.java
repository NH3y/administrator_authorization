
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;

import net.mcreator.administratorauthorization.block.NothingnessBlock;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

public class AdministratorAuthorizationModBlocks {
	public static final DeferredRegister<Block> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCKS, AdministratorAuthorizationMod.MODID);
	public static final RegistryObject<Block> NOTHINGNESS = REGISTRY.register("nothingness", () -> new NothingnessBlock());
	// Start of user code block custom blocks
	// End of user code block custom blocks
}
