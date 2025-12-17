package net.mcreator.administratorauthorization.configuration;


import net.neoforged.neoforge.common.ModConfigSpec;

public class AADestroyerConfiguration {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Boolean> RANGED;
    public static final ModConfigSpec.ConfigValue<Double> RADIUS;
    public static final ModConfigSpec.ConfigValue<Boolean> ACCEPT_MULTIPLE;
    public static final ModConfigSpec.ConfigValue<Integer> SEARCH_DISTANCE;
    public static final ModConfigSpec.ConfigValue<Boolean> KEEP_IN_INVENTORY;
    public static final ModConfigSpec.ConfigValue<Boolean> CAN_DESTROY_BLOCK;
    public static final ModConfigSpec.ConfigValue<Boolean> DROP_BLOCK;
    public static final ModConfigSpec.ConfigValue<Boolean> ACCEPT_INTERACT;

    static {
        BUILDER.push("Targets");
        RANGED = BUILDER.comment("make the special effect of RD affects enemies ina given range").define("Ranged Effect", false);
        RADIUS = BUILDER.comment("the radius of ranged attack").define("Range", (double) 0);
        ACCEPT_MULTIPLE = BUILDER.comment("chose all entity found rather than only one").define("Right Click Accept Multiple Targets", false);
        SEARCH_DISTANCE = BUILDER.comment("The distance of right click search").define("Right Click Distance", 20);
        ACCEPT_INTERACT = BUILDER.comment("Use right click to apply effect by directly interacting with entity").define("Accept Interaction", false);
        BUILDER.pop();
        BUILDER.push("Inventory");
        KEEP_IN_INVENTORY = BUILDER.comment("Prevent RD from being lost or seal").define("Protect RD", false);
        BUILDER.pop();
        BUILDER.push("Blocks");
        CAN_DESTROY_BLOCK = BUILDER.comment("Enable the ability for RD to destroy block").define("Destroy Block", false);
        DROP_BLOCK = BUILDER.comment("Drop the resources when destroy block using RD").define("Drop Block", true);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}
