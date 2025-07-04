
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.BlockItem;

import net.mcreator.administratorauthorization.item.TheSwordItem;
import net.mcreator.administratorauthorization.item.ThePaperItem;
import net.mcreator.administratorauthorization.item.RealityDestroyerItem;
import net.mcreator.administratorauthorization.item.AuthorizerItem;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;

public class AdministratorAuthorizationModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, AdministratorAuthorizationMod.MODID);
	public static final RegistryObject<Item> THE_SWORD = REGISTRY.register("the_sword", () -> new TheSwordItem());
	public static final RegistryObject<Item> THE_PAPER = REGISTRY.register("the_paper", () -> new ThePaperItem());
	public static final RegistryObject<Item> REALITY_DESTROYER = REGISTRY.register("reality_destroyer", () -> new RealityDestroyerItem());
	public static final RegistryObject<Item> AUTHORIZER = REGISTRY.register("authorizer", () -> new AuthorizerItem());
	public static final RegistryObject<Item> NOTHINGNESS = block(AdministratorAuthorizationModBlocks.NOTHINGNESS);

	// Start of user code block custom items
	// End of user code block custom items
	private static RegistryObject<Item> block(RegistryObject<Block> block) {
		return REGISTRY.register(block.getId().getPath(), () -> new BlockItem(block.get(), new Item.Properties()));
	}
}
