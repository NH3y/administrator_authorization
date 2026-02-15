package net.mcreator.administratorauthorization.configuration;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AASecurityConfiguration {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.BooleanValue ENABLED;

    public static final ModConfigSpec.BooleanValue AGENT_DETECT;
    public static final ModConfigSpec.BooleanValue AGENT_SHUTDOWN;
    public static final ModConfigSpec.EnumValue<AgentProcedure> AGENT_PROCEDURE;

    static {
        BUILDER
                .comment("Security Configuration is for detecting and defending some specific java techniques that we usually won't put into the mod")
                .comment("As mod maker, we may have to change game mechanisms using \"Mixin\", which is normal and compatible")
                .comment("but, if some mod trying to remove others' Mixin using higher-leveled tool and thus break AA's protection")
                .comment("you can try toggle some settings here");

        BUILDER.push("common");
        ENABLED = BUILDER.comment("whether to open the security system").define("enabled", true);
        BUILDER.pop();

        BUILDER.push("Java Agent");
        BUILDER
                .comment("Java agent can change every piece of code in anytime while bypass normal detection")
                .comment("if you see something like: \"agent starting\", you should consider checking out the settings here");

        AGENT_SHUTDOWN = BUILDER.comment("close the functionality entirely, but can still fail due to loading order").define("shutdown agent", false);
        AGENT_DETECT = BUILDER.comment("whether to detect agent").define("agent detection", true);
        AGENT_PROCEDURE = BUILDER.comment("What to do after detection").defineEnum("counter measure", AgentProcedure.AUDIT);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    public enum AgentProcedure {
        AUDIT,
        FORCE_SHUTDOWN,
    }
}
