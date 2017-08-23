package crazypants.enderio.conduit.liquid;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import com.enderio.core.common.network.MessageTileEntity;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.util.ClientUtil;

public class PacketFluidLevel extends MessageTileEntity<TileEntity> implements IMessageHandler<PacketFluidLevel, IMessage>{

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
      ClientUtil.doFluidLevelUpdate(message.x, message.y, message.z, message);
      return null;
  }
}
