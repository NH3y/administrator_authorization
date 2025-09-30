package net.mcreator.administratorauthorization.mixins;

import net.mcreator.administratorauthorization.classes.ReflectionUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Mixin(value = EntityDataAccessor.class, priority = Integer.MIN_VALUE)
public abstract class EntityDataAccessorsMixin implements net.mcreator.administratorauthorization.Interfaces.EntityDataAccessorsAccess {
    @Shadow
    public abstract int getId();

    @Unique
    private String administrator_authorization$name = "None";

    @Unique
    private Class<?> administrator_authorization$owner;

    @Unique
    private static final Set<Class<?>> administrator_authorization$loadingClasses = new HashSet<>();

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(int pId, EntityDataSerializer pSerializer, CallbackInfo ci) throws ClassNotFoundException {
        Thread thread = Thread.currentThread();
        StackTraceElement[] stackTrace = thread.getStackTrace();
        for (int i = 1; i < stackTrace.length; i++) {
            if(!stackTrace[i].getClassName().contains("EntityDataAccessor")
                    && !stackTrace[i].getClassName().contains("SynchedEntityData")
                    && !stackTrace[i].getClassName().contains("EntityDataSerializer")
            ){
                Class<?> foundClass = Class.forName(stackTrace[i].getClassName());
                this.administrator_authorization$owner = foundClass;
                administrator_authorization$loadingClasses.add(foundClass);
                break;
            }
        }
    }

    @Override
    public String administrator_authorization$catchName(){
        if(!this.administrator_authorization$name.equals("None"))
            return this.administrator_authorization$name;

        ReflectionUtils utils = new ReflectionUtils(this.administrator_authorization$owner);
        ArrayList<Field> fields = utils.getAllFieldsWithType(EntityDataAccessor.class);
        for (Field field : fields) {
            field.setAccessible(true);
            try{
                EntityDataAccessor<?> entityDataAccessor = (EntityDataAccessor<?>) field.get(null);
                if(entityDataAccessor.getId() == this.getId()){
                    this.administrator_authorization$name = MixinEnvironment.getEnvironment(MixinEnvironment.Phase.INIT).getRemappers().unmap(field.getName());
                    break;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return this.administrator_authorization$name;
    }
}
