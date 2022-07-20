package love.marblegate.creeperfirework.easteregg;

import love.marblegate.creeperfirework.CreeperFirework;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreeperFirework.MOD_ID);
    public static final RegistryObject<Item> CREEPER_HANDMADE_FIREWORK = ITEMS.register("creeper_handmade_firework", HandmadeFirework::new);
}
