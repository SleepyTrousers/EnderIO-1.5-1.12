package crazypants.enderio.machines.machine.soul;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerSoulBinder extends AbstractMachineContainer<TileSoulBinder> implements ISoulBinderRemoteExec.Container {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 2;
  public static int FIRST_INVENTORY_SLOT = 2 + 2 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerSoulBinder(@Nonnull InventoryPlayer playerInv, @Nonnull TileSoulBinder te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 38, 34));
    addSlotToContainer(new InventorySlot(getInv(), 1, 59, 34));
    addSlotToContainer(new Slot(getInv(), 2, 112, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }

      @Override
      public int getSlotStackLimit() {
        return getTe().getInventoryStackLimit(getSlotIndex());
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 134, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }

      @Override
      public int getSlotStackLimit() {
        return getTe().getInventoryStackLimit(getSlotIndex());
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(ModObject.itemSoulVial.getItemNN(), getSlotFromInventory(0)));
    slots.add(new GhostBackgroundItemSlot(ModObject.itemBrokenSpawner.getItemNN(), getSlotFromInventory(1)));
  }

  @Override
  public IMessage doDrainXP(@Nonnull EntityPlayer player, int level) {
    if (player.capabilities.isCreativeMode) {
      getTe().getContainer().addExperience(XpUtil.getExperienceForLevel(level));
      return new PacketExperienceContainer(getTe());
    } else {
      getTe().getContainer().drainPlayerXpToReachContainerLevel(player, level);
      return new PacketExperienceContainer(getTe());
    }
  }

}
