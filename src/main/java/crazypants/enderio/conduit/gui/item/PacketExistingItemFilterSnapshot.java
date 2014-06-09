package crazypants.enderio.conduit.gui.item;

import io.netty.buffer.ByteBuf;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory;
import crazypants.enderio.conduit.item.filter.ExistingItemFilter;
import crazypants.enderio.conduit.packet.AbstractConduitPacket;
import crazypants.enderio.conduit.packet.ConTypeEnum;

public class PacketExistingItemFilterSnapshot extends AbstractConduitPacket<IItemConduit> implements IMessageHandler<PacketExistingItemFilterSnapshot, IMessage> {

  private ForgeDirection dir;
  private boolean isClear;
  private boolean isInput;

  public PacketExistingItemFilterSnapshot() {    
  }
  
  public PacketExistingItemFilterSnapshot(IItemConduit con, ForgeDirection dir, boolean isInput, boolean isClear) {
    super(con.getBundle().getEntity(), ConTypeEnum.ITEM);
    this.dir = dir;
    this.isInput= isInput;
    this.isClear = isClear;
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(buf);
    dir = ForgeDirection.values()[buf.readShort()];
    isInput = buf.readBoolean();
    isClear = buf.readBoolean();   
  }

  @Override
  public void toBytes(ByteBuf buf) {
    super.toBytes(buf);
    buf.writeShort(dir.ordinal());
    buf.writeBoolean(isInput);
    buf.writeBoolean(isClear);    
  }

  @Override
  public PacketExistingItemFilterSnapshot onMessage(PacketExistingItemFilterSnapshot message, MessageContext ctx) {
    IItemConduit conduit = message.getTileCasted(ctx);
    if(conduit == null) {
      return null;
    }
    ExistingItemFilter filter;
    if(message.isInput) {
      filter = (ExistingItemFilter)conduit.getInputFilter(message.dir);  
    } else {
      filter = (ExistingItemFilter)conduit.getOutputFilter(message.dir);
    }
    
    if(message.isClear) {      
      filter.setSnapshot((List<ItemStack>)null);
      System.out.println("PacketExistingItemFilterSnapshot.onMessage: Cleared snapshot");      
    } else {
      ItemConduitNetwork icn = (ItemConduitNetwork)conduit.getNetwork();    
      NetworkedInventory inv = icn.getInventory(conduit, message.dir);
      filter.setSnapshot(inv);        
    }
    
    if(message.isInput) {
      conduit.setInputFilter(message.dir, filter);  
    } else {
      conduit.setOutputFilter(message.dir, filter);
    }
    
    return null;
  }

}
