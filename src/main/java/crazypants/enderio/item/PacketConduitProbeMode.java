package crazypants.enderio.item;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static crazypants.enderio.ModObject.itemConduitProbe;

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
    if (stack != null && stack.getItem() == itemConduitProbe.getItem()) {
      int newMeta = stack.getItemDamage() == 0 ? 1 : 0;
      stack.setItemDamage(newMeta);      
    }
    return null;
  }

}
