package net.mcreator.administratorauthorization;

import net.mcreator.administratorauthorization.configuration.AASecurityConfiguration;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModBlocks;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.init.AttachmentRegistry;
import net.mcreator.administratorauthorization.security.Detector;
import net.mcreator.administratorauthorization.world.dimension.AntiVoidDimension;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

@Mod("administrator_authorization")
public class AdministratorAuthorizationMod {
    public static final Logger LOGGER = LogManager.getLogger(AdministratorAuthorizationMod.class);
    public static final String MODID = "administrator_authorization";

    static {
        System.setProperty("jdk.attach.allowAttachSelf", "false");
    }

    public AdministratorAuthorizationMod(IEventBus modEventBus, ModContainer modContainer) {
        // Start of user code block mod constructor
        // End of user code block mod constructor

        AdministratorAuthorizationModBlocks.REGISTRY.register(modEventBus);
        AdministratorAuthorizationModItems.REGISTRY.register(modEventBus);
        AttachmentRegistry.REGISTER.register(modEventBus);
        modEventBus.addListener(this::registerNetworking);
        // Start of user code block mod init
        // End of user code block mod init
        if (
                AASecurityConfiguration.ENABLED.get() &&
                AASecurityConfiguration.AGENT_DETECT.get()
        ) {
            Detector.startAttachMonitoring();
        }
    }

    private static boolean networkingRegistered = false;
    private static final Map<CustomPacketPayload.Type<?>, NetworkMessage<?>> MESSAGES = new HashMap<>();

    private record NetworkMessage<T extends CustomPacketPayload>(StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
    }

    public static <T extends CustomPacketPayload> void addNetworkMessage(CustomPacketPayload.Type<T> id, StreamCodec<? extends FriendlyByteBuf, T> reader, IPayloadHandler<T> handler) {
        if (networkingRegistered)
            throw new IllegalStateException("Cannot register new network messages after networking has been registered");
        MESSAGES.put(id, new NetworkMessage<>(reader, handler));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void registerNetworking(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(MODID);
        MESSAGES.forEach((id, networkMessage) -> registrar.playBidirectional(id, ((NetworkMessage) networkMessage).reader(), ((NetworkMessage) networkMessage).handler()));
        networkingRegistered = true;
    }
}
