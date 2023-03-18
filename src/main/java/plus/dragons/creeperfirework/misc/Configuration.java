package plus.dragons.creeperfirework.misc;

import net.minecraftforge.common.ForgeConfigSpec;

public class Configuration {

    public static final ForgeConfigSpec MOD_CONFIG;

    public static ForgeConfigSpec.BooleanValue ACTIVE_EXPLOSION_TO_FIREWORK;
    public static ForgeConfigSpec.BooleanValue ACTIVE_EXPLOSION_DESTROY_BLOCK;
    public static ForgeConfigSpec.BooleanValue ACTIVE_EXPLOSION_HURT_CREATURE;
    public static ForgeConfigSpec.DoubleValue ACTIVE_EXPLOSION_TURNING_PROBABILITY;
    public static ForgeConfigSpec.BooleanValue DEATH_TO_FIREWORK;
    public static ForgeConfigSpec.BooleanValue DEATH_EXPLOSION_DESTROY_BLOCK;
    public static ForgeConfigSpec.BooleanValue DEATH_EXPLOSION_HURT_CREATURE;
    public static ForgeConfigSpec.DoubleValue DEATH_EXPLOSION_TURNING_PROBABILITY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("active_explosion");
        ACTIVE_EXPLOSION_TO_FIREWORK = builder.comment("Will creeper's active-explosion turn into firework?").define("ACTIVE_EXPLOSION_TO_FIREWORK", true);
        ACTIVE_EXPLOSION_DESTROY_BLOCK = builder.comment("Will the active-explosion firework destroy nearby environment just like creeper normally exploding?").define("ACTIVE_EXPLOSION_DESTROY_BLOCK", false);
        ACTIVE_EXPLOSION_HURT_CREATURE = builder.comment("Will the active-explosion firework effect hurt nearby creature?").define("ACTIVE_EXPLOSION_HURT_CREATURE", false);
        ACTIVE_EXPLOSION_TURNING_PROBABILITY = builder.comment("The probability of creeper turning into firework when actively explodes.").defineInRange("ACTIVE_EXPLOSION_TURNING_PROBABILITY", 1.0, 0, 1.0);
        builder.pop();

        builder.push("death_explosion");
        DEATH_TO_FIREWORK = builder.comment("Will creeper explode into firework when die?").define("DEATH_TO_FIREWORK", false);
        DEATH_EXPLOSION_DESTROY_BLOCK = builder.comment("Will the death-explosion firework destroy nearby environment just like creeper normally exploding?").define("DEATH_EXPLOSION_DESTROY_BLOCK", false);
        DEATH_EXPLOSION_HURT_CREATURE = builder.comment("Will the death-explosion firework effect hurt nearby creature?").define("DEATH_EXPLOSION_HURT_CREATURE", false);
        DEATH_EXPLOSION_TURNING_PROBABILITY = builder.comment("The probability of creeper turning into firework when die.").defineInRange("DEATH_EXPLOSION_TURNING_PROBABILITY", 1.0, 0, 1.0);
        builder.pop();

        MOD_CONFIG = builder.build();
    }
}
