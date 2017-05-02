package crazypants.enderio.teleport.telepad.packet;

import com.enderio.core.common.util.ItemUtil;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketUpdateLocationPrintout implements IMessage, IMessageHandler<PacketUpdateLocationPrintout, IMessage> {

  private ItemStack stack;
  private EntityEquipmentSlot slot;
 
  public PacketUpdateLocationPrintout() {    
  }
  
  public PacketUpdateLocationPrintout(ItemStack stack, EntityEquipmentSlot slot) {  
    this.stack = stack;
    this.slot = slot;
  }

  @Override
  public void toBytes(ByteBuf buf) {    
    ByteBufUtils.writeItemStack(buf, stack);
    int ord = -1;
    if(slot != null) {
      ord = slot.ordinal();
    }
    buf.writeShort(ord);    
  }
  
  @Override
  public void fromBytes(ByteBuf buf) {
    stack = ByteBufUtils.readItemStack(buf);
    int ord = buf.readShort();
    if(ord < 0 || ord >= EntityEquipmentSlot.values().length) {
      slot = null;
    } else {
      slot = EntityEquipmentSlot.values()[ord];
    }    
  }

  @Override
  public IMessage onMessage(PacketUpdateLocationPrintout message, MessageContext ctx) { 
    EntityPlayerMP player = ctx.getServerHandler().playerEntity;    
    ItemStack stack = message.stack;
    EntityEquipmentSlot slot = message.slot;
    if(stack == null) {
      return null;
    }
    if(slot != null) {
      player.setItemStackToSlot(slot, stack);
      return null;
    }     
    if (!ctx.getServerHandler().playerEntity.inventory.addItemStackToInventory(stack)) {
      ItemUtil.spawnItemInWorldWithRandomMotion(player.world, stack, player.getPosition());
    }
    return null;
  }

}
