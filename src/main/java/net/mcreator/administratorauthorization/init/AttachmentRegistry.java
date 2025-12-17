package net.mcreator.administratorauthorization.init;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.attachment.HealthData;
import net.mcreator.administratorauthorization.attachment.InventorySlotData;
import net.mcreator.administratorauthorization.attachment.RouterData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentRegistry {
    public static final DeferredRegister<AttachmentType<?>> REGISTER = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, AdministratorAuthorizationMod.MODID);

    public static final Supplier<AttachmentType<HealthData>> HEALTH_DATA = REGISTER.register("health_data", () ->
            AttachmentType.serializable(HealthData::new).build()
    );

    public static final Supplier<AttachmentType<InventorySlotData>> INVENTORY_SLOT_DATA = REGISTER.register("inventory_slot_data", () ->
            AttachmentType.serializable(InventorySlotData::new).build()
    );

    public static final Supplier<AttachmentType<RouterData>> ROUTER_DATA = REGISTER.register("router_data", () ->
            AttachmentType.serializable(RouterData::new).build()
    );
}
