package dev.qedi.heightlimiter;

import com.mojang.logging.LogUtils;
import dev.qedi.heightlimiter.config.ServerConfig;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod(HeightLimiterMod.MODID)
public class HeightLimiterMod {
    public static final String MODID = "heightlimiter";
    private static final Logger LOGGER = LogUtils.getLogger();

    public HeightLimiterMod() {
        LOGGER.info("HeightLimiter present");
        ServerConfig.register(ModLoadingContext.get());
    }
}
