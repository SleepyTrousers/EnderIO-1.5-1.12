package crazypants.enderio.conduit.packet;

import crazypants.enderio.conduit.liquid.EnderLiquidConduit;
import crazypants.enderio.conduit.liquid.FluidFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketFluidFilter extends AbstractConduitPacket<ILiquidConduit> implements IMessageHandler<PacketFluidFilter, IMessage>{

  private EnumFacing dir;
  private boolean isInput;
  private FluidFilter filter;
  
  public PacketFluidFilter() {    
  }
  
  public PacketFluidFilter(EnderLiquidConduit eConduit, EnumFacing dir, FluidFilter filter, boolean isInput) {
    super(eConduit.getBundle().getEntity(), eConduit);
    this.dir = dir;
    this.filter = filter;
    this.isInput = isInput;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    if(dir != null) {
      buf.writeShort(dir.ordinal());
    } else {
      buf.writeShort(-1);
    }
    buf.writeBoolean(isInput);
    NBTTagCompound tag = new NBTTagCompound();
    filter.writeToNBT(tag);
    ByteBufUtils.writeTag(buf, tag);
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    short ord = buf.readShort();
    if(ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    isInput = buf.readBoolean();    
    NBTTagCompound tag = ByteBufUtils.readTag(buf);
    filter = new FluidFilter();
    filter.readFromNBT(tag);
  }

  @Override
  public IMessage onMessage(PacketFluidFilter message, MessageContext ctx) {
    ILiquidConduit conduit = message.getTileCasted(ctx);
    if(! (conduit instanceof EnderLiquidConduit)) {
      return null;
    }    
    EnderLiquidConduit eCon = (EnderLiquidConduit)conduit;
    eCon.setFilter(message.dir, message.filter, message.isInput);
        
    IBlockState bs = message.getWorld(ctx).getBlockState(message.getPos());
    message.getWorld(ctx).notifyBlockUpdate(message.getPos(), bs, bs, 3);    
    return null;
  }

}
