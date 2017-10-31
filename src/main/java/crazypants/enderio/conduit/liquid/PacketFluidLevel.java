package crazypants.enderio.conduit.liquid;

import com.enderio.core.EnderCore;
import com.enderio.core.common.network.MessageTileEntity;

import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidLevel extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketFluidLevel, IMessage> {// TODO: DONE111

  public NBTTagCompound tc;

  public PacketFluidLevel() {
  }

  public PacketFluidLevel(ILiquidConduit conduit) {
    super(conduit.getBundle().getEntity());
    tc = new NBTTagCompound();
    conduit.writeToNBT(tc);
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    ByteBufUtils.writeTag(buf, tc);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    tc = ByteBufUtils.readTag(buf);
  }

  @Override
  public IMessage onMessage(PacketFluidLevel message, MessageContext ctx) {
    TileEntity tile = message.getTileEntity(EnderCore.proxy.getClientWorld());
    if (message.tc == null || !(tile instanceof IConduitBundle)) {
      return null;
    }
    IConduitBundle bundle = (IConduitBundle) tile;
    ILiquidConduit con = bundle.getConduit(ILiquidConduit.class);
    if (con == null) {
      return null;
    }
    con.readFromNBT(message.tc, TileConduitBundle.NBT_VERSION);
    return null;
  }
}
