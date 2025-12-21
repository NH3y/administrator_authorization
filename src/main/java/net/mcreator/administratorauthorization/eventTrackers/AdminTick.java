package net.mcreator.administratorauthorization.eventTrackers;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.AttributeAccess;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashSet;
import java.util.Set;

@EventBusSubscriber
public class AdminTick {
    private static final Multimap<Holder<Attribute>, AttributeModifier> bannedModifiers = MultimapBuilder.hashKeys().arrayListValues().build();

    @SubscribeEvent
    public static void minutePre(PlayerTickEvent.Pre event) {
        Player entity = event.getEntity();
        if (!((EntityAccess) entity).administrator_authorization$getAuthorization()) return;

        entity.getAttributes().removeAttributeModifiers(bannedModifiers);

        if (entity.tickCount % 1200 == 0) {
            ((AttributeAccess) entity.getAttributes()).administrator_authorization$getAllAttributes()
                    .forEach((key, value) -> {
                        if (value != null) {
                            Set<AttributeModifier> toRemove = new HashSet<>();
                            value.getModifiers().forEach((modifier) -> {
                                if (
                                        (modifier.amount() == 0 && modifier.operation().equals(AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)) ||
                                        (modifier.amount() < 0 && modifier.operation().equals(AttributeModifier.Operation.ADD_VALUE))
                                ) {
                                    toRemove.add(modifier);
                                }
                            });

                            for (AttributeModifier modifier : toRemove) {
                                bannedModifiers.put(key, modifier);
                                AdministratorAuthorizationMod.LOGGER.
                                        warn("Modifier {} on attribute {} has been removed for negative effect", modifier.id(), key.getRegisteredName());
                            }
                            toRemove.forEach(value::removeModifier);
                        }
                    });
        }
    }

    @SubscribeEvent
    public static void minutePost(PlayerTickEvent.Post event) {
        Player entity = event.getEntity();
        if (!((EntityAccess) entity).administrator_authorization$getAuthorization()) return;

        if (entity.tickCount % 1200 == 0) {
            //To Complete
        }
    }
}
