package crazypants.enderio.machines.machine.soul;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.client.gui.widget.GhostSlot;

import crazypants.enderio.base.init.ModObject;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.base.network.GuiPacket;
import crazypants.enderio.base.network.IRemoteExec;
import crazypants.enderio.base.xp.PacketExperienceContainer;
import crazypants.enderio.base.xp.XpUtil;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerSoulBinder extends AbstractMachineContainer<TileSoulBinder> implements IRemoteExec.IContainer {

  public static final int ADD_XP = 0;

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
    addSlotToContainer(new Slot(getInv(), 0, 38, 34) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }

      @Override
      public void putStack(@Nonnull ItemStack stack) {
        if (stack.getCount() <= getItemStackLimit(stack)) {
          super.putStack(stack);
        } else {
          throw new RuntimeException("Invalid stacksize. " + stack.getCount() + " is more than the allowed limit of " + getItemStackLimit(stack)
              + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
        }
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 59, 34) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }

      @Override
      public void putStack(@Nonnull ItemStack stack) {
        if (stack.getCount() <= getItemStackLimit(stack)) {
          super.putStack(stack);
        } else {
          throw new RuntimeException("Invalid stacksize. " + stack.getCount() + " is more than the allowed limit of " + getItemStackLimit(stack)
              + ". THIS IS NOT AN ERROR IN ENDER IO BUT THE CALLING MOD!");
        }
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 112, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 134, 34) {
      @Override
      public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
        return false;
      }
    });
  }

  public void createGhostSlots(List<GhostSlot> slots) {
    slots.add(new GhostBackgroundItemSlot(ModObject.itemSoulVial.getItemNN(), getSlotFromInventory(0)));
    slots.add(new GhostBackgroundItemSlot(ModObject.itemBrokenSpawner.getItemNN(), getSlotFromInventory(1)));
  }

  @Override
  public IMessage networkExec(int id, GuiPacket message) {
    if (id == ADD_XP) {
      if (message.getPlayer().capabilities.isCreativeMode) {
        getTe().getContainer().addExperience(XpUtil.getExperienceForLevel(message.getInt(0)));
        return new PacketExperienceContainer(getTe());
      } else {
        getTe().getContainer().drainPlayerXpToReachContainerLevel(message.getPlayer(), message.getInt(0));
        return new PacketExperienceContainer(getTe());
      }
    }
    return null;
  }

}
