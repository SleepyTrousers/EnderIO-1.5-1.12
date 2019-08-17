package crazypants.enderio.base.handler.darksteel.gui;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public interface ISlotSelector {

  boolean isAnvil();

  boolean isItem();

  boolean isSlot();

  @Nonnull
  EntityEquipmentSlot getSlot();

  int getTabOrder();

  @Nonnull
  ItemStack getItem(@Nonnull EntityPlayer player);

  @Nonnull
  Slot setContainerSlot(@Nonnull Slot containerSlot);

  Slot getContainerSlot();

}
