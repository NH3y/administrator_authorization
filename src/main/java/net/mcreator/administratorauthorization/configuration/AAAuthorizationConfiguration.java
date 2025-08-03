package net.mcreator.administratorauthorization.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AAAuthorizationConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Boolean> LOCK_HEALTH;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_NEUTRAL;
	public static final ForgeConfigSpec.ConfigValue<Boolean> RECORD_DEATH_POS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> BAN_SUSPECT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> CORRUPT_EVENT;
	public static final ForgeConfigSpec.ConfigValue<Boolean> SPACE_INTERFERE;
	static {
		LOCK_HEALTH = BUILDER.comment("To lock the player's health to resist special setHealth methods").define("lock health", false);
		BAN_NEUTRAL = BUILDER.comment("Avoid neutral effects").define("no neutral effects", false);
		BAN_SUSPECT = BUILDER.comment("Ban ANY data change with float which is commonly use only for health (might cause problem if not)").define("no float data change", false);
		CORRUPT_EVENT = BUILDER.comment("Cancel ANY event whose name contains \"hurt\" or \"death\"").define("corrupt event", false);
		SPACE_INTERFERE = BUILDER.comment("Interfere space to avoid being selected by most of attack").define("space interfere", false);

		BUILDER.push("Exception Dealing");
		BUILDER.comment("Exceptions means players still die even when the authorization is on");
		BUILDER.comment("This should be reported as bug, but before it being fixed, use options here for temporary convenience");
		BUILDER.comment("");
		RECORD_DEATH_POS = BUILDER.comment("To respawn player at the same spot you died. Use when the death is inevitable").define("fake death", false);

		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
