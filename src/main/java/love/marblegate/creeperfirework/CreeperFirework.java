package love.marblegate.creeperfirework;

import love.marblegate.creeperfirework.easteregg.ItemRegistry;
import love.marblegate.creeperfirework.misc.Configuration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod("creeper_firework")
public class CreeperFirework {
    public static String MOD_ID = "creeper_firework";

    public CreeperFirework() {
        ItemRegistry.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.MOD_CONFIG);
    }

}
