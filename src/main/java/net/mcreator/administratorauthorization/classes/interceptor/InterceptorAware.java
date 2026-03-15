package net.mcreator.administratorauthorization.classes.interceptor;

public interface InterceptorAware {
    Interceptor getInterceptor();
    void setInterceptor(Interceptor interceptor);

    HaltingValve getHaltingValve();
    void setHaltingValve(HaltingValve haltingValve);
}
