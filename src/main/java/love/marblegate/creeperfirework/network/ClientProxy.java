package love.marblegate.creeperfirework.network;

import love.marblegate.creeperfirework.misc.FireworkManufacturer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class ClientProxy implements IProxy {
    @Override
    public void handlePacket(BlockPos pos, boolean powered) {
        Minecraft.getInstance().level.createFireworks(pos.getX(), pos.getY() + 0.5F, pos.getZ(), 0, 0, 0, FireworkManufacturer.generate(powered));
        if (powered) {
            Minecraft.getInstance().level.createFireworks(pos.getX(), pos.getY() + 2.5F, pos.getZ(), 0, 0, 0, FireworkManufacturer.generateRandomSpecial());
        }
        if (powered) {
            Minecraft.getInstance().level.playSound((Player) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIREWORK_ROCKET_LARGE_BLAST, SoundSource.HOSTILE, 8.0F, 2.0F);
        } else {
            Minecraft.getInstance().level.playSound((Player) null, pos.getX(), pos.getY(), pos.getZ(), SoundEvents.FIREWORK_ROCKET_BLAST, SoundSource.HOSTILE, 8.0F, 2.0F);
        }
    }
}
