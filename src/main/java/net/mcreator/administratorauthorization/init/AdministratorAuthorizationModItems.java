
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.item.AuthorizerItem;
import net.mcreator.administratorauthorization.item.RealityDestroyerItem;
import net.mcreator.administratorauthorization.item.ThePaperItem;
import net.mcreator.administratorauthorization.item.TheSwordItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class AdministratorAuthorizationModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, AdministratorAuthorizationMod.MODID);
    public static final RegistryObject<Item> THE_SWORD = REGISTRY.register("the_sword", TheSwordItem::new);
    public static final RegistryObject<Item> THE_PAPER = REGISTRY.register("the_paper", ThePaperItem::new);
    public static final RegistryObject<Item> REALITY_DESTROYER = REGISTRY.register("reality_destroyer", RealityDestroyerItem::new);
    public static final RegistryObject<Item> AUTHORIZER = REGISTRY.register("authorizer", AuthorizerItem::new);
    public static final RegistryObject<Item> NOTHINGNESS = block();

    // Start of user code block custom items
    // End of user code block custom items
    private static RegistryObject<Item> block() {
        if (AdministratorAuthorizationModBlocks.NOTHINGNESS.getId() != null) {
            return REGISTRY.register(AdministratorAuthorizationModBlocks.NOTHINGNESS.getId().getPath(), () -> new BlockItem(AdministratorAuthorizationModBlocks.NOTHINGNESS.get(), new Item.Properties()));
        }
        return null;
    }
}
