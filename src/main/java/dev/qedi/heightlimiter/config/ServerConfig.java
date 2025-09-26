package dev.qedi.heightlimiter.config;

import dev.qedi.heightlimiter.HeightLimiterMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfig {
    public static class Common {
        public static ForgeConfigSpec.ConfigValue<Integer> HEIGHT_LIMIT_TOP = null;
        public static ForgeConfigSpec.ConfigValue<Integer> HEIGHT_LIMIT_BOTTOM = null;
        public static ForgeConfigSpec.ConfigValue<String> RESTRICTED_CATEGORIES = null;
        public static ForgeConfigSpec.ConfigValue<Boolean> LOGGING = null;

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("HeightLimiter Mod Config")
                    .push("common");

            HEIGHT_LIMIT_TOP = builder
                    .comment("\n Mobs shouldn't spawn between top and bottom borders.")
                    .define("top_height_limit", 60);

            HEIGHT_LIMIT_BOTTOM = builder
                    .comment("\n Bottom limit after that mobs allow to spawn.")
                    .define("bottom_height_limit", -64);

            RESTRICTED_CATEGORIES = builder
                    .comment("\n MODIDs from what mobs restricted to spawn.")
                    .define("limited", "['abominations_infection']");

            LOGGING = builder
                    .comment("\n Write if entity spawn prevented")
                    .define("write_logs", true);

            builder.pop();
        }
    }

    private static final ForgeConfigSpec commonSpec;
    public static final Common COMMON;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        commonSpec = specPair.getRight();
        COMMON = specPair.getLeft();
    }

    public static void register(final ModLoadingContext context) {
        context.registerConfig(ModConfig.Type.COMMON, commonSpec, HeightLimiterMod.MODID+".toml");
    }


}