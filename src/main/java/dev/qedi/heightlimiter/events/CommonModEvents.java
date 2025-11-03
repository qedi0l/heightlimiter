package dev.qedi.heightlimiter.events;

import com.mojang.logging.LogUtils;
import dev.qedi.heightlimiter.HeightLimiterMod;
import dev.qedi.heightlimiter.config.ServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.living.MobSpawnEvent.FinalizeSpawn;
import net.minecraftforge.eventbus.api.Event;
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
        boolean logging = ServerConfig.Common.LOGGING.get();
        Entity entity = event.getEntity();
        int entityX = (int) entity.getX();
        int entityY = (int) entity.getY();
        int entityZ = (int) entity.getZ();

        if (shouldCancelSpawn(entity)) {
            event.setCanceled(true);
            event.setSpawnCancelled(true);
            event.isCanceled();
            event.setResult(Event.Result.DENY);

            if (logging) {
                LOGGER.info("Spawn prevented {} At XYZ {} {} {}", getEntityModIdAndName(entity), entityX, entityY, entityZ);
            }
        }
    }

    private static boolean shouldCancelSpawn(Entity entity) {
        int topLimit = ServerConfig.Common.HEIGHT_LIMIT_TOP.get();
        int bottomLimit = ServerConfig.Common.HEIGHT_LIMIT_BOTTOM.get();
        boolean customCategoriesEnable = ServerConfig.Common.CUSTOM_CATEGORIES_ENABLE.get();
        String[] restrictedMods = arrayFromString(ServerConfig.Common.RESTRICTED_MODS.get());
        ArrayList<String> customCategoriesList = ServerConfig.Common.CUSTOM_CATEGORIES.get();
        int entityY = (int) entity.getY();

        if (entityFromRestrictedMod(entity, restrictedMods)) {
            if (entityY >= bottomLimit && entityY <= topLimit ) {
                return true;
            }
        } else {
            if (customCategoriesEnable){
                int[] tmp = entityFromRestrictedCategoryList(entity,customCategoriesList);
                if (tmp[0] == 1){ // check bool
                   if (entityY >= tmp[1] && entityY <= tmp[2]) { // [1] - top [2] - bottom
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static int[] entityFromRestrictedCategoryList(Entity entity, ArrayList<String> customCategoriesList) {
        for (String config : customCategoriesList) {
            String[] parts = config.split("%");

            if(parts.length > 0){
                String entitiesArray = parts[0];
                int bottomLimit = Integer.parseInt(parts[1]);
                int topLimit = Integer.parseInt(parts[2]);

                if (entitiesArray.contains(getEntityModIdAndName(entity))){
                    return new int[] {1, bottomLimit, topLimit};
                }
            }
        }
        return new int[]{0,0,0};
    }

    private static boolean entityFromRestrictedMod(Entity entity, String[] mods) {
        return Arrays.asList(mods).contains(getEntityModId(entity));
    }

    private static String getEntityModId(Entity entity) {
        ResourceLocation registryName = entity.getType().builtInRegistryHolder().key().location();

        return registryName.getNamespace();
    }

    private static String getEntityModIdAndName(Entity entity) {
        ResourceLocation registryName = entity.getType().builtInRegistryHolder().key().location();

        return registryName.getNamespace() + ":" + registryName.getPath();
    }

    // AI slop I don't read
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
