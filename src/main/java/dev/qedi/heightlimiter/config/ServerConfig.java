package dev.qedi.heightlimiter.config;

import dev.qedi.heightlimiter.HeightLimiterMod;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class ServerConfig {
    public static class Common {
        public static ForgeConfigSpec.ConfigValue<Integer> HEIGHT_LIMIT_TOP = null;
        public static ForgeConfigSpec.ConfigValue<Integer> HEIGHT_LIMIT_BOTTOM = null;
        public static ForgeConfigSpec.ConfigValue<String> RESTRICTED_MODS = null;
        public static ForgeConfigSpec.ConfigValue<ArrayList<String>> CUSTOM_CATEGORIES = null;
        public static ForgeConfigSpec.ConfigValue<Boolean> CUSTOM_CATEGORIES_ENABLE = null;
        public static ForgeConfigSpec.ConfigValue<Boolean> LOGGING = null;
        private static final ArrayList<String> categories = new ArrayList<>(
                List.of(
                    "[mod:example1,mod:example2]%-64%64"
                )
        );

        Common(final ForgeConfigSpec.Builder builder) {
            builder.comment("HeightLimiter Mod Config")
                    .push("common");

            HEIGHT_LIMIT_TOP = builder
                    .comment("\n Global limit to global restrictions")
                    .define("top_height_limit", 60);

            HEIGHT_LIMIT_BOTTOM = builder
                    .define("bottom_height_limit", -64);

            RESTRICTED_MODS = builder
                    .comment("\n Global restriction")
                    .define("limited", "['abominations_infection']");

            CUSTOM_CATEGORIES_ENABLE = builder
                    .comment("\n custom categories enable")
                    .define("custom_categories_enable", false);

            CUSTOM_CATEGORIES  = builder
                    .comment("\n Custom categories, fomrat like this [array of mobs]%bottom_limit%top_limit. \n You can add another array inside.")
                    .define("categories", categories);

            LOGGING = builder
                    .comment("\n Spam in your logs? :)")
                    .define("write_logs", false);

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