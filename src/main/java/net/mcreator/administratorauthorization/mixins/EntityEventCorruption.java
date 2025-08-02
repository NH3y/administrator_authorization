package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.AdministratorAuthorizationMod;
import net.mcreator.administratorauthorization.Interfaces.EntityAccess;
import net.mcreator.administratorauthorization.classes.Tracker;
import net.mcreator.administratorauthorization.configuration.AAAuthorizationConfiguration;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.event.entity.EntityEvent;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = EntityEvent.class, priority = Integer.MIN_VALUE, remap = false)
public class EntityEventCorruption extends Event implements net.mcreator.administratorauthorization.Interfaces.VirusAccess {
    @Mutable
    @Shadow @Final private Entity entity;

    @Unique
    private boolean administrator_authorization$virus;

    @Inject(method = "<init>", at = @At("TAIL"))
    public void EntityEvent(Entity entity, CallbackInfo ci) {
        if(this.entity != null) {
            this.administrator_authorization$virus = ((EntityAccess) this.entity).administrator_authorization$getAuthorization();
            if (this.administrator_authorization$virus && administrator_authorization$checkBadEvent(this.getClass())) {
                if(this.isCancelable()) {
                    this.setCanceled(true);
                    AdministratorAuthorizationMod.LOGGER.info("Mixin : event self-canceled");
                }else if(this.hasResult()){
                    this.setResult(Result.DENY);
                    AdministratorAuthorizationMod.LOGGER.info("Mixin : event self-denied");
                }
                administrator_authorization$setFloat(
                        this.getClass(),
                        "amount",
                        0.0f
                );
                Tracker.registerEntityCanceled(((EntityEvent)(Object)(this)).getClass());
            }
        }
    }

    @Unique
    private boolean administrator_authorization$checkBadEvent(Class<? extends EntityEventCorruption> aClass) {
        if(!AAAuthorizationConfiguration.CORRUPT_EVENT.get()){
            return false;
        }
        String name = aClass.getName().toLowerCase();
        return name.contains("hurt") || name.contains("death");
    }

    @Override
    public boolean administrator_authorization$checkVirus(){
        return this.administrator_authorization$virus;
    }
    
    @Unique
    private void administrator_authorization$setFloat(Class<? extends Event> classA, String path, float value ){
        Class classNow = classA;
        while(classNow.getSuperclass() != null){
            try{
                Field field = classNow.getDeclaredField(path);
                field.setAccessible(true);
                field.setFloat(this, value);
                AdministratorAuthorizationMod.LOGGER.info("Set {} successfully", path);
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
            classNow = classNow.getSuperclass();
        }
    }

    @Override
    public Set<Class<? extends Event>> administrator_authorization$getAllSuperClass(Class<? extends EntityEvent> target){
        Set<Class<? extends Event>> classes = new HashSet<>();
        classes.add(target);
        Class current = target.getSuperclass();
        while(current.getSuperclass() != null){
            classes.add(current);
            current = current.getSuperclass();
        }
        return classes;
    }
}
