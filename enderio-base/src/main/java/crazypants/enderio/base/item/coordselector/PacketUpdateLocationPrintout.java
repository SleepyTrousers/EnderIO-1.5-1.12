package crazypants.enderio.base.item.coordselector;

import javax.annotation.Nonnull;

import crazypants.enderio.util.Prep;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import static crazypants.enderio.base.init.ModObject.itemLocationPrintout;

public class PacketUpdateLocationPrintout implements IMessage {

  private @Nonnull ItemStack stack = Prep.getEmpty();
  private EntityEquipmentSlot slot;
  private int paperSlot;

  public PacketUpdateLocationPrintout() {
  }

  public PacketUpdateLocationPrintout(@Nonnull ItemStack stack, EntityEquipmentSlot slot, int paperSlot) {
    this.stack = stack;
    this.slot = slot;
    this.paperSlot = paperSlot;
  }

  @Override
  public void toBytes(ByteBuf buf) {
    ByteBufUtils.writeItemStack(buf, stack);
    int ord = -1;
    if (slot != null) {
      ord = slot.ordinal();
    }
    buf.writeShort(ord);
    buf.writeShort(paperSlot);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    stack = ByteBufUtils.readItemStack(buf);
    int ord = buf.readShort();
    if (ord < 0 || ord >= EntityEquipmentSlot.values().length) {
      slot = null;
    } else {
      slot = EntityEquipmentSlot.values()[ord];
    }
    paperSlot = buf.readShort();
  }

  public static class Handler implements IMessageHandler<PacketUpdateLocationPrintout, IMessage> {

    @Override
    public IMessage onMessage(PacketUpdateLocationPrintout message, MessageContext ctx) {
      EntityPlayerMP player = ctx.getServerHandler().player;
      ItemStack stack = message.stack;
      EntityEquipmentSlot slot = message.slot;
      int paperSlot = message.paperSlot;
      if (Prep.isInvalid(stack) || stack.getItem() != itemLocationPrintout.getItem()) {
        return null;
      }
      TelepadTarget telepadTarget = TelepadTarget.readFromNBT(stack);
      if (telepadTarget == null) {
        return null;
      }
      if (slot != null) {
        updatePrintout(player, slot, telepadTarget);
      } else if (paperSlot >= 0) {
        createPrintout(player, telepadTarget, paperSlot);
      }
      return null;
    }

    private void createPrintout(@Nonnull EntityPlayerMP player, @Nonnull TelepadTarget telepadTarget, int paperSlot) {
      if (telepadTarget.getDimension() != player.world.provider.getDimension()) {
        return;
      }
      if (telepadTarget.getLocation().distanceSq(new BlockPos(player)) > 160 * 160) {
        // ideally we'd want to raytrace this, but the difference between raytracing on the server and the client is just too big, especially over long
        // distances...
        return;
      }
      ItemStack invItem = player.inventoryContainer.inventorySlots.get(paperSlot).getStack();
      if (Prep.isValid(invItem) && invItem.getItem() == Items.PAPER) {
        player.inventoryContainer.inventorySlots.get(paperSlot).decrStackSize(1);
        player.inventoryContainer.detectAndSendChanges();
        ItemStack stack = new ItemStack(itemLocationPrintout.getItemNN());
        telepadTarget.writeToNBT(stack);
        if (!player.inventory.addItemStackToInventory(stack)) {
          player.dropItem(stack, false);
        }
      }
    }

    private void updatePrintout(@Nonnull EntityPlayerMP player, @Nonnull EntityEquipmentSlot slot, @Nonnull TelepadTarget telepadTarget) {
      ItemStack existingStack = player.getItemStackFromSlot(slot);
      if (Prep.isInvalid(existingStack) || existingStack.getItem() != itemLocationPrintout.getItem()) {
        return;
      }
      TelepadTarget existingTarget = TelepadTarget.readFromNBT(existingStack);
      if (existingTarget == null) {
        return;
      }
      existingTarget.setName(telepadTarget.getName());
      existingTarget.writeToNBT(existingStack);
      player.setItemStackToSlot(slot, existingStack);
    }

  }

}
