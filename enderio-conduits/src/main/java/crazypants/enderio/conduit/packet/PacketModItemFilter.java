package crazypants.enderio.conduit.packet;

import crazypants.enderio.base.filter.filters.ModItemFilter;
import crazypants.enderio.conduit.item.IItemConduit;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketModItemFilter extends AbstractConduitPacket<IItemConduit> implements IMessageHandler<PacketModItemFilter, IMessage> {

  private EnumFacing dir;  
  private boolean isInput;
  private int index;
  private String name;

  public PacketModItemFilter() {    
  }
  
  public PacketModItemFilter(IItemConduit con, EnumFacing dir, boolean isInput, int index, String name) {
    super(con.getBundle().getEntity(), con);
    this.dir = dir;
    this.isInput= isInput;
    this.index = index;
    this.name = name;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    int ord = buf.readShort();
    if(ord < 0) {
      dir = null;
    } else {
      dir = EnumFacing.values()[ord];
    }
    isInput = buf.readBoolean();
    index = buf.readInt();   
    boolean isNull = buf.readBoolean();
    if(isNull) {
      name = null;
    } else {
      name = ByteBufUtils.readUTF8String(buf);
    }
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
    buf.writeInt(index);
    buf.writeBoolean(name == null);
    if(name != null) {
      ByteBufUtils.writeUTF8String(buf, name);
    }
  }

  @Override
  public IMessage onMessage(PacketModItemFilter message, MessageContext ctx) {
    IItemConduit conduit = message.getTileCasted(ctx);
    if(conduit == null) {
      return null;
    }
    ModItemFilter filter;
    if(message.isInput) {
      filter = (ModItemFilter)conduit.getInputFilter(message.dir);  
    } else {
      filter = (ModItemFilter)conduit.getOutputFilter(message.dir);
    }
    
    if (message.index == -1) {
      filter.setBlacklist("1".equals(message.name));
    } else {
      filter.setMod(message.index, message.name);
    }
    
    if(message.isInput) {
      conduit.setInputFilter(message.dir, filter);  
    } else {
      conduit.setOutputFilter(message.dir, filter);
    }
    
    return null;
  }
}
