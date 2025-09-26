package dev.qedi.heightlimiter.events;

import com.mojang.logging.LogUtils;
import dev.qedi.heightlimiter.HeightLimiterMod;
import dev.qedi.heightlimiter.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber(modid = HeightLimiterMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class CommonModEvents {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public static void onEntitySpawn(FinalizeSpawn event) {
        if (shouldCancelSpawn(event)) {
            event.setCanceled(true);
        }
    }

    private static boolean shouldCancelSpawn(FinalizeSpawn event) {
        Entity entity = event.getEntity();
        String modid = getEntityModId(entity);

        int topLimit = ServerConfig.Common.HEIGHT_LIMIT_TOP.get();
        int bottomLimit = ServerConfig.Common.HEIGHT_LIMIT_BOTTOM.get();
        boolean logging = ServerConfig.Common.LOGGING.get();
        String[] categories = arrayFromString(ServerConfig.Common.RESTRICTED_CATEGORIES.get());

        if (entityFromRestrictedMod(entity, categories)) {
            int entityY = (int) entity.getY();

            if (entityY >= bottomLimit && entityY <= topLimit ) {
                if (logging) {
                    LOGGER.info("Spawn prevented " + modid + " entity " + entity.getName() + " At Y = " + entityY);
                }

                return true;
            }
        }
        return false;
    }

    private static boolean entityFromRestrictedMod(Entity entity, String[] categories) {
        return Arrays.asList(categories).contains(getEntityModId(entity));
    }

    private static String getEntityModId(Entity entity) {
        ResourceLocation registryName = entity.getType().builtInRegistryHolder().key().location();

        return registryName.getNamespace();
    }

    // AI slop I don't read TODO refactor
    public static String[] arrayFromString(String input) {
        Pattern pattern = Pattern.compile("'(.*?)'");
        Matcher matcher = pattern.matcher(input);

        List<String> list = new ArrayList<>();
        while (matcher.find()) {
            list.add(matcher.group(1));
        }

        String[] result = list.toArray(new String[0]);
        return result;
    }
}
