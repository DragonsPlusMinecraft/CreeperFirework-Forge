package love.marblegate.creeperfirework;

import love.marblegate.creeperfirework.misc.Configuration;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod("creeper_firework")
public class CreeperFirework {

    public CreeperFirework() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.MOD_CONFIG);
    }

}
