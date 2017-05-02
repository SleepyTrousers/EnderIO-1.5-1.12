package crazypants.enderio.machine.alloy;

import javax.annotation.Nullable;

import com.enderio.core.common.util.Util;

import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.network.GuiPacket;
import crazypants.enderio.network.IRemoteExec;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.AchievementList;

public class ContainerAlloySmelter extends AbstractMachineContainer<TileAlloySmelter> implements IRemoteExec.IContainer {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 3;
  public static int FIRST_INVENTORY_SLOT = 3 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  private final EntityPlayer thePlayer;

  public ContainerAlloySmelter(InventoryPlayer playerInv, TileAlloySmelter te) {
    super(playerInv, te);
    thePlayer = playerInv.player;
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 54, 17) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 79, 7) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 103, 17) {
      @Override
      public boolean isItemValid(@Nullable ItemStack itemStack) {
        return getInv().isItemValidForSlot(2, itemStack);
      }
    });
    addSlotToContainer(new SlotSmelter(getInv(), 3, 79, 57));

  }

  private class SlotSmelter extends Slot {

    int numResults = 0;

    public SlotSmelter(IInventory par1iInventory, int par2, int par3, int par4) {
      super(par1iInventory, par2, par3, par4);
    }

    @Override
    public ItemStack decrStackSize(int par1) {
      if (getHasStack()) {
        numResults += Math.min(par1, getStack().stackSize);
      }
      return super.decrStackSize(par1);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
      return false;
    }

    @Override
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack output) {
      onCrafting(output);
      super.onPickupFromSlot(par1EntityPlayer, output);
    }

    @Override
    protected void onCrafting(ItemStack par1ItemStack, int par2) {
      numResults += par2;
      onCrafting(par1ItemStack);
    }

    @Override
    protected void onCrafting(ItemStack output) {
      output.onCrafting(thePlayer.worldObj, thePlayer, numResults);
      if (!thePlayer.worldObj.isRemote) {
        ItemStack outputSized = output.copy();
        outputSized.stackSize = numResults;
        float experience = getInv().getExperienceForOutput(outputSized);
        Util.giveExperience(thePlayer, experience);
      }
      numResults = 0;

      if (output.getItem() == Items.IRON_INGOT) {
        thePlayer.addStat(AchievementList.ACQUIRE_IRON, 1);
      }
      if (output.getItem() == Items.COOKED_FISH) {
        thePlayer.addStat(AchievementList.COOK_FISH, 1);
      }
    }
  }

  @Override
  public void networkExec(int id, GuiPacket message) {
    switch (id) {
    case 0:
      getInv().setMode(message.getEnum(0, TileAlloySmelter.Mode.class));
      IBlockState bs = getInv().getWorld().getBlockState(getInv().getPos());
      getInv().getWorld().notifyBlockUpdate(getInv().getPos(), bs, bs, 3);
      break;
    default:
      break;
    }
  }

}
