package crazypants.enderio.machine.farm;

import com.enderio.core.common.util.BlockCoord;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.util.ClientUtil;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PacketFarmAction implements IMessage, IMessageHandler<PacketFarmAction, IMessage> {

    private static Random rand = new Random();

    private List<BlockCoord> coords;

    public PacketFarmAction() {}

    public PacketFarmAction(List<BlockCoord> coords) {
        this.coords = coords;
    }

    public PacketFarmAction(BlockCoord bc) {
        this.coords = new ArrayList<BlockCoord>(1);
        this.coords.add(bc);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        int size = coords.size();
        buffer.writeInt(size);
        for (BlockCoord coord : coords) {
            buffer.writeInt(coord.x);
            buffer.writeInt(coord.y);
            buffer.writeInt(coord.z);
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer) {
        int size = buffer.readInt();
        coords = new ArrayList<BlockCoord>(size);
        for (int i = 0; i < size; i++) {
            coords.add(new BlockCoord(buffer.readInt(), buffer.readInt(), buffer.readInt()));
        }
    }

    @Override
    public IMessage onMessage(PacketFarmAction message, MessageContext ctx) {
        for (BlockCoord bc : message.coords) {
            for (int i = 0; i < 15; i++) {
                ClientUtil.spawnFarmParcticles(rand, bc);
            }
        }
        return null;
    }
}
