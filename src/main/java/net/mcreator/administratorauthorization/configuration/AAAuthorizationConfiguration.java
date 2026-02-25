package net.mcreator.administratorauthorization.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AAAuthorizationConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> LOCK_DATA;
    public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_NEUTRAL;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RECORD_DEATH_POS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SPACE_INTERFERE;
    public static final ForgeConfigSpec.BooleanValue COMMAND_PROTECT;
    public static final ForgeConfigSpec.BooleanValue PENETRATION;
    public static final ForgeConfigSpec.BooleanValue CLEAR_DIRECTLY;

    static {
        COMMAND_PROTECT = BUILDER.comment("Prevent command attacks from players without authorization").define("command protect", false);
        LOCK_DATA = BUILDER.comment("To lock the player's float data to resist special setHealth methods").define("lock data", false);
        BAN_NEUTRAL = BUILDER.comment("Avoid neutral effects").define("no neutral effects", false);
        SPACE_INTERFERE = BUILDER.comment("Interfere space to avoid being selected by most of attack").define("space interfere", false);

        BUILDER.push("Data Penetration");
        PENETRATION = BUILDER.comment("Avoid all the reduction on damage caused by admin").define("Penetration", false);
        CLEAR_DIRECTLY = BUILDER
                .comment("If any mod modified the die() method, it will cause fake death on every entity admin kill")
                .comment("Enable this to clear those entity (no loot)")
                .define("clear directly", false);
        BUILDER.pop();

        BUILDER.push("Exception Dealing");
        BUILDER
                .comment("Exceptions means players still die even when the authorization is on")
                .comment("This should be reported as bug, but before it being fixed, use options here for temporary convenience")
                .comment("");
        RECORD_DEATH_POS = BUILDER.comment("To respawn player at the same spot you died. Use when the death is inevitable").define("fake death", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
