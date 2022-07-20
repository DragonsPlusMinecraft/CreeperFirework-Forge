package love.marblegate.creeperfirework.misc;

import com.google.common.collect.Lists;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Random;

public class FireworkManufacturer {
    public static CompoundTag generate(boolean powered) {
        CompoundTag fireworkInfoNbt = generateColorNBT();
        fireworkInfoNbt.putBoolean("Flicker", powered);
        fireworkInfoNbt.putByte("Type", (byte) (powered ? 1 : 0));
        fireworkInfoNbt.putBoolean("Trail", powered);
        ListTag mimicFireworkItemNbtStructureContainer = new ListTag();
        mimicFireworkItemNbtStructureContainer.add(fireworkInfoNbt);

        CompoundTag ret = new CompoundTag();
        ret.put("Explosions", mimicFireworkItemNbtStructureContainer);
        return ret;
    }

    public static ItemStack generateHandmade(ItemStack fireworkItemStack) {
        var ret = fireworkItemStack.copy();
        var random = new Random();
        CompoundTag fireworkInfoNbt = new CompoundTag();
        CompoundTag singleIngredientNbt;
        ListTag mimicFireworkItemNbtStructureContainer = new ListTag();
        singleIngredientNbt = generateColorNBT();
        singleIngredientNbt.putBoolean("Flicker", random.nextBoolean());
        singleIngredientNbt.putByte("Type", (byte) (random.nextInt(4)+1));
        singleIngredientNbt.putBoolean("Trail", true);
        mimicFireworkItemNbtStructureContainer.add(singleIngredientNbt);
        fireworkInfoNbt.put("Explosions", mimicFireworkItemNbtStructureContainer);
        ByteTag byteTag = ByteTag.valueOf((byte) (2 + random.nextInt(2)));
        fireworkInfoNbt.put("Flight", byteTag);
        ret.addTagElement("Fireworks",fireworkInfoNbt);
        return ret;
    }

    public static CompoundTag generateRandomSpecial() {
        CompoundTag fireworkInfoNbt = generateColorNBT();
        fireworkInfoNbt.putByte("Type", (byte) (new Random().nextInt(3) + 2));
        ListTag mimicFireworkItemNbtStructureContainer = new ListTag();
        mimicFireworkItemNbtStructureContainer.add(fireworkInfoNbt);

        CompoundTag ret = new CompoundTag();
        ret.put("Explosions", mimicFireworkItemNbtStructureContainer);
        return ret;
    }

    private static CompoundTag generateColorNBT() {
        CompoundTag fireworkInfoNbt = new CompoundTag();

        // Generate Color From DyeColor
        // Maximum is 8 dyes for a firework star
        List<Integer> list = Lists.newArrayList();
        Random rand = new Random();
        int dyeCount = rand.nextInt(8) + 1;
        for (int i = 0; i < dyeCount; i++)
            list.add(DyeColor.values()[rand.nextInt(DyeColor.values().length)].getFireworkColor());

        // Transform color array into required structure
        int[] colours = new int[list.size()];
        for (int i = 0; i < colours.length; i++)
            colours[i] = list.get(i);

        fireworkInfoNbt.putIntArray("Colors", colours);
        return fireworkInfoNbt;
    }
}
