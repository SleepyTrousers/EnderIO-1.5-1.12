package crazypants.enderio.machines.machine.alloy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.Util;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class ContainerAlloySmelter<T extends TileAlloySmelter> extends AbstractMachineContainer<T> implements IAlloySmelterRemoteExec.Container {

  public static class Enhanced extends ContainerAlloySmelter<TileAlloySmelter.Enhanced> {
    public Enhanced(@Nonnull InventoryPlayer playerInv, @Nonnull TileAlloySmelter.Enhanced te) {
      super(playerInv, te);
    }
  }

  public static class Normal extends ContainerAlloySmelter<TileAlloySmelter> {
    public Normal(@Nonnull InventoryPlayer playerInv, @Nonnull TileAlloySmelter te) {
      super(playerInv, te);
    }
  }

  public static class Simple extends ContainerAlloySmelter<TileAlloySmelter.Simple> {
    public Simple(@Nonnull InventoryPlayer playerInv, @Nonnull TileAlloySmelter.Simple te) {
      super(playerInv, te);
    }
  }

  public static class Furnace extends ContainerAlloySmelter<TileAlloySmelter.Furnace> {
    public Furnace(@Nonnull InventoryPlayer playerInv, @Nonnull TileAlloySmelter.Furnace te) {
      super(playerInv, te);
    }
  }

  @SuppressWarnings("unchecked")
  public static @Nonnull <E extends TileAlloySmelter> ContainerAlloySmelter<E> create(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    if (te instanceof TileAlloySmelter.Simple) {
      return (ContainerAlloySmelter<E>) new Simple(playerInv, (TileAlloySmelter.Simple) te);
    } else if (te instanceof TileAlloySmelter.Furnace) {
      return (ContainerAlloySmelter<E>) new Furnace(playerInv, (TileAlloySmelter.Furnace) te);
    } else if (te instanceof TileAlloySmelter.Enhanced) {
      return (ContainerAlloySmelter<E>) new Enhanced(playerInv, (TileAlloySmelter.Enhanced) te);
    } else {
      return (ContainerAlloySmelter<E>) new Normal(playerInv, te);
    }
  }

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 3;
  public static int FIRST_INVENTORY_SLOT = 3 + 1 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  private final @Nonnull EntityPlayer player;

  private ContainerAlloySmelter(@Nonnull InventoryPlayer playerInv, @Nonnull T te) {
    super(playerInv, te);
    player = playerInv.player;
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
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

    public SlotSmelter(@Nonnull IInventory par1iInventory, int par2, int par3, int par4) {
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
      FMLCommonHandler.instance().firePlayerSmeltedEvent(player, output);
    }
  }

  @Override
  public IMessage doSetMode(@Nonnull OperatingMode mode) {
    getTe().setMode(mode);
    IBlockState bs = getTe().getWorld().getBlockState(getTe().getPos());
    getTe().getWorld().notifyBlockUpdate(getTe().getPos(), bs, bs, 3);
    return null;
  }

}
