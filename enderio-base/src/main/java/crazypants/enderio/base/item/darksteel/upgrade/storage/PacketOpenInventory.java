package crazypants.enderio.base.item.darksteel.upgrade.storage;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.ItemDarkSteelArmor;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenInventory implements IMessage {

  public PacketOpenInventory() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<PacketOpenInventory, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenInventory message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      SlotEncoder param1 = new SlotEncoder();
      for (EntityEquipmentSlot slot : StorageData.ARMOR) {
        int index = slot.getIndex();
        ItemStack stack = player.inventory.armorInventory.get(index);
        int slots = stack.getItem() instanceof ItemDarkSteelArmor ? StorageData.slots(slot, 1) : 0; // TODO check upgrade
        param1.set(slot, slots);
      }
      if (param1.hasSlots()) {
        ModObject.itemDarkSteelChestplate.openGui(player.world, player, param1.getValue(), 0, 0);
      }
      return null;
    }
  }
}
