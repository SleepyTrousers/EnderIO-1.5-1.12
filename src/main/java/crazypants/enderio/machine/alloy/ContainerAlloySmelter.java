package crazypants.enderio.machine.alloy;

import javax.annotation.Nonnull;
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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class ContainerAlloySmelter extends AbstractMachineContainer<TileAlloySmelter> implements IRemoteExec.IContainer {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 3;
  public static int FIRST_INVENTORY_SLOT = 3 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  private final @Nonnull EntityPlayer player;

  public ContainerAlloySmelter(@Nonnull InventoryPlayer playerInv, @Nonnull TileAlloySmelter te) {
    super(playerInv, te);
    player = playerInv.player;
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new Slot(getInv(), 0, 54, 17) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(0, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 1, 79, 7) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
        return getInv().isItemValidForSlot(1, itemStack);
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 103, 17) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack itemStack) {
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
    public @Nonnull ItemStack decrStackSize(int par1) {
      if (getHasStack()) {
        numResults += Math.min(par1, getStack().getCount());
      }
      return super.decrStackSize(par1);
    }

    @Override
    public boolean isItemValid(@Nullable ItemStack par1ItemStack) {
      return false;
    }

    @Override
    public @Nonnull ItemStack onTake(@Nonnull EntityPlayer par1EntityPlayer, @Nonnull ItemStack output) {
      onCrafting(output);
      return super.onTake(par1EntityPlayer, output);
    }

    @Override
    protected void onCrafting(@Nonnull ItemStack par1ItemStack, int par2) {
      numResults += par2;
      onCrafting(par1ItemStack);
    }

    @Override
    protected void onCrafting(@Nonnull ItemStack output) {
      output.onCrafting(player.world, player, numResults);
      if (!player.world.isRemote) {
        ItemStack outputSized = output.copy();
        outputSized.setCount(numResults);
        float experience = getTe().getExperienceForOutput(outputSized);
        Util.giveExperience(player, experience);
      }
      numResults = 0;

      if (output.getItem() == Items.IRON_INGOT) {
        player.addStat(AchievementList.ACQUIRE_IRON, 1);
      }
      if (output.getItem() == Items.COOKED_FISH) {
        player.addStat(AchievementList.COOK_FISH, 1);
      }
    }
  }

  @Override
  public IMessage networkExec(int id, GuiPacket message) {
    switch (id) {
    case 0:
      getTe().setMode(message.getEnum(0, TileAlloySmelter.Mode.class));
      IBlockState bs = getTe().getWorld().getBlockState(getTe().getPos());
      getTe().getWorld().notifyBlockUpdate(getTe().getPos(), bs, bs, 3);
      break;
    default:
      break;
    }
    return null;
  }

}
