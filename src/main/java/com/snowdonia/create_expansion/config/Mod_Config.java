package com.snowdonia.create_expansion.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Server-side configuration for Create Expansion, written to
 * {@code <world>/serverconfig/create_expansion-server.toml}.
 *
 * <p>Values are synced to connected clients, so progress bars in the machine GUIs
 * stay in step with whatever processing time the server is running.
 */
public class Mod_Config {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    /** How many ticks the Water Strainer takes to complete one production cycle. */
    public static final ModConfigSpec.IntValue WATER_STRAINER_PROCESSING_TICKS;
    /** How many ticks the Bedrock Extractor takes to complete one production cycle. */
    public static final ModConfigSpec.IntValue BEDROCK_EXTRACTOR_PROCESSING_TICKS;
    /** Stress impact (su per RPM) the Reverse Motor draws from the kinetic network. */
    public static final ModConfigSpec.DoubleValue REVERSE_MOTOR_STRESS_IMPACT;
    /** Forge Energy generated per tick, per RPM of the Reverse Motor's shaft. */
    public static final ModConfigSpec.DoubleValue REVERSE_MOTOR_FE_PER_RPM;
    /** Size of the Reverse Motor's internal energy buffer (FE). */
    public static final ModConfigSpec.IntValue REVERSE_MOTOR_ENERGY_CAPACITY;
    /** Maximum FE the Reverse Motor pushes out per face per tick. */
    public static final ModConfigSpec.IntValue REVERSE_MOTOR_MAX_OUTPUT;

    public static final ModConfigSpec SPEC;

    static {
        BUILDER.comment("Processing speed of the mod's generation blocks.")
                .push("processing");

        // Capped at 1 minute (1200 ticks). This also stays within a signed short,
        // which matters because ContainerData syncs progress to the GUI as a short.
        WATER_STRAINER_PROCESSING_TICKS = BUILDER
                .comment("Ticks the Water Strainer takes to produce once (20 ticks = 1 second, max 1 minute).")
                .defineInRange("waterStrainerProcessingTicks", 200, 1, 1200);

        BEDROCK_EXTRACTOR_PROCESSING_TICKS = BUILDER
                .comment("Ticks the Bedrock Extractor takes to produce once (20 ticks = 1 second, max 1 minute).")
                .defineInRange("bedrockExtractorProcessingTicks", 200, 1, 1200);

        BUILDER.pop();

        BUILDER.comment("Kinetic blocks.")
                .push("kinetics");

        // Balance anchor: at 256 su/rpm a motor draws a full Steam Engine's 16,384 SU at
        // 64 RPM, and total FE/tick per engine = 16384 * fePerRpm / stressImpact — so this
        // pair yields ~256 FE/t per Steam Engine (~410k FE per coal), independent of gearing.
        REVERSE_MOTOR_STRESS_IMPACT = BUILDER
                .comment("Rotational force the Reverse Motor draws from the network (stress impact, su per RPM).")
                .defineInRange("reverseMotorStressImpact", 256.0, 0.0, 16384.0);

        REVERSE_MOTOR_FE_PER_RPM = BUILDER
                .comment("Forge Energy the Reverse Motor generates per tick, per RPM of its shaft.")
                .defineInRange("reverseMotorFePerRpm", 4.0, 0.0, 100000.0);

        REVERSE_MOTOR_ENERGY_CAPACITY = BUILDER
                .comment("Size of the Reverse Motor's internal energy buffer (FE).")
                .defineInRange("reverseMotorEnergyCapacity", 10000, 0, Integer.MAX_VALUE);

        REVERSE_MOTOR_MAX_OUTPUT = BUILDER
                .comment("Maximum FE the Reverse Motor pushes out per face each tick.")
                .defineInRange("reverseMotorMaxOutput", 10000, 0, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

    private Mod_Config() {
    }
}
