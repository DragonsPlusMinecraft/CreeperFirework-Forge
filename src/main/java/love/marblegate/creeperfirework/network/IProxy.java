package love.marblegate.creeperfirework.network;

import net.minecraft.core.BlockPos;

public interface IProxy {
    void handlePacket(BlockPos pos, boolean powered);
}
