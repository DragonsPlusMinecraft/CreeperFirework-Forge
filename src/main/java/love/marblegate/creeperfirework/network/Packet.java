package love.marblegate.creeperfirework.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class Packet {
    private final BlockPos pos;
    private final boolean powered;
    private IProxy proxy;

    public Packet(FriendlyByteBuf buffer) {
        pos = buffer.readBlockPos();
        powered = buffer.readBoolean();
    }

    public Packet(BlockPos pos, boolean powered) {
        this.pos = pos;
        this.powered = powered;
    }

    public void toBytes(FriendlyByteBuf buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(powered);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            proxy = new ClientProxy();
            ctx.get().enqueueWork(() -> proxy.handlePacket(pos, powered));
            ctx.get().setPacketHandled(true);
        });

    }
}
