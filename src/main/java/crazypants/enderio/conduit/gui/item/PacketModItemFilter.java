package crazypants.enderio.conduit.gui.item;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.filter.ModItemFilter;
import crazypants.enderio.conduit.packet.AbstractConduitPacket;
import crazypants.enderio.conduit.packet.ConTypeEnum;

public class PacketModItemFilter extends AbstractConduitPacket<IItemConduit> implements IMessageHandler<PacketModItemFilter, IMessage> {

  private ForgeDirection dir;  
  private boolean isInput;
  private int index;
  private String name;

  public PacketModItemFilter() {    
  }
  
  public PacketModItemFilter(IItemConduit con, ForgeDirection dir, boolean isInput, int index, String name) {
    super(con.getBundle().getEntity(), ConTypeEnum.ITEM);
    this.dir = dir;
    this.isInput= isInput;
    this.index = index;
    this.name = name;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = ForgeDirection.values()[buf.readShort()];
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
    buf.writeShort(dir.ordinal());
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
