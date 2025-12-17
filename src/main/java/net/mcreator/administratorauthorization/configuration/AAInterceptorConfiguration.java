package net.mcreator.administratorauthorization.configuration;


import net.neoforged.neoforge.common.ModConfigSpec;

public class AAInterceptorConfiguration {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> GET_HEALTH;
    public static final ModConfigSpec.ConfigValue<Boolean> IS_DEAD;
    public static final ModConfigSpec.ConfigValue<Boolean> IS_ALIVE;
    public static final ModConfigSpec.ConfigValue<Boolean> SET_HEALTH;
    public static final ModConfigSpec.ConfigValue<Boolean> DIE;
    public static final ModConfigSpec.ConfigValue<Boolean> IS_IMMOBILE;
    public static final ModConfigSpec.ConfigValue<Boolean> REMOVE;

    static {
        BUILDER
                .comment("Interceptor is a technique that can build one of the strongest defend line")
                .comment("However, it can also break other mods functionalities sometimes")
                .comment("To decide whether to toggle following options, check if other mods' mechanism is essential in gameplay as administrator");

        BUILDER.push("Living Entity Interceptor");

        GET_HEALTH = BUILDER.comment("Still get hurt as administrator? try turn this on!").define("intercept getHealth", false);
        IS_DEAD = BUILDER.comment("Still get dead screen as administrator? try turn this on!").define("intercept isDeadOrDying", false);
        IS_ALIVE = BUILDER.comment("Can't open containers while being alive? try turn this on!").define("intercept isAlive", false);
        SET_HEALTH = BUILDER.comment("Only get hurt when attacked by certain entity? try turn this on!").define("intercept setHealth", false);
        DIE = BUILDER.comment("Instantly killed without even realizing? try turn this on!").define("intercept die", false);
        IS_IMMOBILE = BUILDER.comment("Can't move while being Alive? try turn this on!").define("intercept isImmobile", false);
        REMOVE = BUILDER.comment("Respawn without getting hurt? try turn this on!").define("intercept remove (reason:KILLED)", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
