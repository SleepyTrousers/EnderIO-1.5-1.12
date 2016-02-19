package crazypants.enderio.item;

import crazypants.enderio.EnderIO;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketConduitProbeMode implements IMessage, IMessageHandler<PacketConduitProbeMode, IMessage>  {

  public PacketConduitProbeMode() {    
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
  }

  @Override
  public IMessage onMessage(PacketConduitProbeMode message, MessageContext ctx) {    
    ItemStack stack = ctx.getServerHandler().playerEntity.inventory.getCurrentItem();    
    if(stack != null && stack.getItem() == EnderIO.itemConduitProbe) {
      int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
      stack.setItemDamage(newMeta);      
    }
    return null;
  }

}
