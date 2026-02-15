package net.mcreator.administratorauthorization.configuration;


import net.neoforged.neoforge.common.ModConfigSpec;

public class AAAuthorizationConfiguration {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> SHOW_ADMIN;
    public static final ModConfigSpec.ConfigValue<Integer> REQUIRED_LEVEL;
    public static final ModConfigSpec.ConfigValue<Boolean> LOCK_DATA;
    public static final ModConfigSpec.ConfigValue<Boolean> BAN_NEUTRAL;
    public static final ModConfigSpec.ConfigValue<Boolean> RECORD_DEATH_POS;
    public static final ModConfigSpec.ConfigValue<Boolean> SPACE_INTERFERE;
    public static final ModConfigSpec.BooleanValue COMMAND_PROTECT;

    static {

        SHOW_ADMIN = BUILDER.comment("Show \"Admin\" at right downside of screen if you are").define("show admin", false);
        REQUIRED_LEVEL = BUILDER.comment("The permission levels required by AA items").define("required level", 2);

        BUILDER.push("Protection");
        COMMAND_PROTECT = BUILDER.comment("Prevent command attacks from players without authorization").define("command protect", false);
        LOCK_DATA = BUILDER.comment("To lock the player's float data to resist special setHealth methods").define("lock data", false);
        BAN_NEUTRAL = BUILDER.comment("Avoid neutral effects").define("no neutral effects", false);
        SPACE_INTERFERE = BUILDER.comment("Interfere space to avoid being selected by most of attack").define("space interfere", false);
        BUILDER.pop();

        BUILDER.push("Exception Dealing");
        BUILDER.comment("Exceptions means players still die even when the authorization is on");
        BUILDER.comment("This should be reported as bug, but before it being fixed, use options here for temporary convenience");
        BUILDER.comment("");
        RECORD_DEATH_POS = BUILDER.comment("To respawn player at the same spot you died. Use when the death is inevitable").define("fake death", false);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
