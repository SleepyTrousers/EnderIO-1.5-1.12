package crazypants.enderio.base.handler.darksteel.gui;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.item.darksteel.upgrade.storage.SlotEncoder;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketOpenDSU implements IMessage {

  public PacketOpenDSU() {
  }

  @Override
  public void toBytes(ByteBuf buf) {
  }

  @Override
  public void fromBytes(ByteBuf buf) {
  }

  public static class Handler implements IMessageHandler<PacketOpenDSU, IMessage> {

    @Override
    public IMessage onMessage(PacketOpenDSU message, MessageContext ctx) {
      EntityPlayer player = ctx.getServerHandler().player;
      SlotEncoder param1 = new SlotEncoder();
      // for (EntityEquipmentSlot slot : StorageUpgrade.ARMOR) {
      // param1.set(slot, StorageUpgrade.slots(slot, StorageUpgrade.INSTANCE.getUpgradeVariantLevel(player.inventory.armorInventory.get(slot.getIndex()))));
      // }
      // if (param1.hasSlots()) {
      // Note: The GUI is bound to ModObject.itemDarkSteelChestplate, but that is just for technical reasons. It supports any armor item with this upgrade
      ModObject.itemDarkSteelPickaxe.openGui(player.world, player, param1.getValue(), 0, 0);
      // } else {
      // player.sendStatusMessage(Lang.GUI_NO_ARMOR_INVENTORY.toChatServer(), true);
      // }
      return null;
    }
  }
}
