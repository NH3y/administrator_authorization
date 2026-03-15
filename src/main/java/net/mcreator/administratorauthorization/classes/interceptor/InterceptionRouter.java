package net.mcreator.administratorauthorization.classes.interceptor;

import net.bytebuddy.implementation.bind.annotation.*;
import net.mcreator.administratorauthorization.classes.Invoker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.Callable;

public class InterceptionRouter {
    private static final Map<String, Method> INTERCEPTION_METHODS = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InterceptionRouter.class);

    @RuntimeType
    public static Object intercept(
            @This InterceptorAware self,
            @Origin Method origin,
            @AllArguments(nullIfEmpty = true) Object[] args,
            @SuperCall Callable<?> superCall
    ) {
        Interceptor interceptor;
        ArrayList<Object> list;
        try {
            interceptor = self.getInterceptor();

            if (interceptor == null) {
                self.setInterceptor(new CommonInterceptor());
                return superCall.call();
            }

            if (!INTERCEPTION_METHODS.containsKey(origin.getName())) {
                INTERCEPTION_METHODS.put(
                        origin.getName(),
                        Arrays.stream(Interceptor.class.getDeclaredMethods())
                                .filter(method -> method.getName().equals(origin.getName()))
                                .findFirst()
                                .orElseThrow()
                );
            }
            list = args == null ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args));
            list.add(self);
            list.add(superCall);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error(e.getCause().getMessage());
            throw new RuntimeException(e);
        }
        return Invoker.getInvoker(origin.getParameterCount() + 2).invoke(interceptor, INTERCEPTION_METHODS.get(origin.getName()), list.toArray());
    }
}
