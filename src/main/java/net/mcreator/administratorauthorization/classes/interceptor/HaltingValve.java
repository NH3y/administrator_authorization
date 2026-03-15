package net.mcreator.administratorauthorization.classes.interceptor;

import com.google.common.base.Defaults;
import net.bytebuddy.implementation.bind.annotation.*;

import java.lang.invoke.MethodType;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;

public class HaltingValve {
    private Set<String> haltingMethods = new HashSet<>();

    @RuntimeType
    public static Object valve(
            @This InterceptorAware self,
            @SuperCall Callable<?> original,
            @Origin MethodType name
    ) throws Exception {
        Class<?> returnType = name.returnType();
        HaltingValve valve = self.getHaltingValve();
        Object call = returnType == Void.TYPE ? null : original.call();
        if (valve == null) {
            self.setHaltingValve(new HaltingValve());
            return call == null ? original.call() : call;
        }
        if (valve.haltingMethods.contains("ALL_METHODS") || valve.haltingMethods.contains(name.toString())) {
            if (call == null) {
                return call;
            }
            if (returnType.isPrimitive()) {
                return Defaults.defaultValue(returnType);
            }
            return call;
        }
        return call == null ? original.call() : call;
    }

    public Set<String> getHaltingMethods() {
        return haltingMethods;
    }

    public void addAll() {
        this.haltingMethods.add("ALL_METHODS");
    }

    public void setHaltingMethods(Set<String> haltingMethods) {
        this.haltingMethods = haltingMethods;
    }
}
