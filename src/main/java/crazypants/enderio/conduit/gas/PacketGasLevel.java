package crazypants.enderio.conduit.gas;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.util.ClientUtil;

public class PacketGasLevel extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketGasLevel, IMessage> {

  public NBTTagCompound tc;

  public PacketGasLevel() {
  }

  public PacketGasLevel(IGasConduit conduit) {
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
  public IMessage onMessage(PacketGasLevel message, MessageContext ctx) {
    ClientUtil.doGasLevelUpdate(message.x, message.y, message.z, message);
    return null;
  }
}
