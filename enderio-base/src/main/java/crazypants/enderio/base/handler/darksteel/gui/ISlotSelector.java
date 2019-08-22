package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public interface ISlotSelector {

  boolean isAnvil();

  boolean isItem();

  boolean isSlot();

  EntityEquipmentSlot getSlot();

  int getTabOrder();

  ItemStack getItem(EntityPlayer player);

  Slot setContainerSlot(Slot containerSlot);

  @Nullable
  Slot getContainerSlot();

}
