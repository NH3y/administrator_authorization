
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.item.AuthorizerItem;
import net.mcreator.administratorauthorization.item.RealityDestroyerItem;
import net.mcreator.administratorauthorization.item.ThePaperItem;
import net.mcreator.administratorauthorization.item.TheSwordItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AdministratorAuthorizationModItems {
    public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, AdministratorAuthorizationMod.MODID);
    public static final DeferredHolder<Item, TheSwordItem> THE_SWORD = REGISTRY.register("the_sword", TheSwordItem::new);
    public static final DeferredHolder<Item, ThePaperItem> THE_PAPER = REGISTRY.register("the_paper", ThePaperItem::new);
    public static final DeferredHolder<Item, RealityDestroyerItem> REALITY_DESTROYER = REGISTRY.register("reality_destroyer", RealityDestroyerItem::new);
    public static final DeferredHolder<Item, AuthorizerItem> AUTHORIZER = REGISTRY.register("authorizer", AuthorizerItem::new);
    public static final DeferredHolder<Item, BlockItem> NOTHINGNESS = block();

    // Start of user code block custom items
    // End of user code block custom items
    private static DeferredHolder<Item, BlockItem> block() {
        return REGISTRY.register(AdministratorAuthorizationModBlocks.NOTHINGNESS.getId().getPath(), () -> new BlockItem(AdministratorAuthorizationModBlocks.NOTHINGNESS.get(), new Item.Properties()));
    }
}
