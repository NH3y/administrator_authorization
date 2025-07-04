package net.mcreator.administratorauthorization.configuration;

import net.minecraftforge.common.ForgeConfigSpec;

public class AADestroyerConfiguration {
	public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
	public static final ForgeConfigSpec SPEC;

	public static final ForgeConfigSpec.ConfigValue<Boolean> RANGED;
	public static final ForgeConfigSpec.ConfigValue<Double> RADIUS;
	public static final ForgeConfigSpec.ConfigValue<Boolean> ACCEPT_MULTIPLE;
	public static final ForgeConfigSpec.ConfigValue<Integer> SEARCH_DISTANCE;
	public static final ForgeConfigSpec.ConfigValue<Boolean> KEEP_IN_INVENTORY;
	static {
		BUILDER.push("Targets");
		RANGED = BUILDER.comment("make the special effect of RD affects enemies ina given range").define("Ranged Effect", false);
		RADIUS = BUILDER.comment("the radius of ranged attack").define("Range", (double) 0);
		ACCEPT_MULTIPLE = BUILDER.comment("chose all entity found rather than only one").define("Right Click Accept Multiple Targets", false);
		SEARCH_DISTANCE = BUILDER.comment("The distance of right click search").define("Right Click Distance", 20);
		BUILDER.pop();
		BUILDER.push("Inventory");
		KEEP_IN_INVENTORY = BUILDER.comment("Prevent RD from being lost or seal").define("Protect RD", false);
		BUILDER.pop();

		SPEC = BUILDER.build();
	}

}
