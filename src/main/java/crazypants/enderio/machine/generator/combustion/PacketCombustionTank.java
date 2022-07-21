package crazypants.enderio.machine.generator.combustion;

import com.enderio.core.common.network.MessageTileEntity;
import com.enderio.core.common.network.NetworkUtil;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.util.ClientUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

public class PacketCombustionTank extends MessageTileEntity<TileCombustionGenerator>
        implements IMessageHandler<PacketCombustionTank, IMessage> {

    public NBTTagCompound nbtRoot;

    public PacketCombustionTank() {}

    public PacketCombustionTank(TileCombustionGenerator tile) {
        super(tile);
        nbtRoot = new NBTTagCompound();
        if (tile.getCoolantTank().getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            tile.getCoolantTank().writeToNBT(tankRoot);
            nbtRoot.setTag("coolantTank", tankRoot);
        }
        if (tile.getFuelTank().getFluidAmount() > 0) {
            NBTTagCompound tankRoot = new NBTTagCompound();
            tile.getFuelTank().writeToNBT(tankRoot);
            nbtRoot.setTag("fuelTank", tankRoot);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        super.toBytes(buf);
        NetworkUtil.writeNBTTagCompound(nbtRoot, buf);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        super.fromBytes(buf);
        nbtRoot = NetworkUtil.readNBTTagCompound(buf);
    }

    @Override
    public IMessage onMessage(PacketCombustionTank message, MessageContext ctx) {
        ClientUtil.setTankNBT(message, message.x, message.y, message.z);
        return null;
    }
}
