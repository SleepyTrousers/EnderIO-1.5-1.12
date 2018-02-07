package crazypants.enderio.machines.machine.sagmill;

import javax.annotation.Nonnull;

import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.machines.machine.tank.InventorySlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSagMill<E extends TileSagMill> extends AbstractMachineContainer<E> {

  public static class Normal extends ContainerSagMill<TileSagMill.Normal> {
    public Normal(@Nonnull InventoryPlayer playerInv, @Nonnull TileSagMill.Normal te) {
      super(playerInv, te);
    }
  }

  public static class Simple extends ContainerSagMill<TileSagMill.Simple> {
    public Simple(@Nonnull InventoryPlayer playerInv, @Nonnull TileSagMill.Simple te) {
      super(playerInv, te);
    }
  }

  @SuppressWarnings("unchecked")
  public static @Nonnull <E extends TileSagMill> ContainerSagMill<E> create(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    if (te instanceof TileSagMill.Simple) {
      return (ContainerSagMill<E>) new Simple(playerInv, (TileSagMill.Simple) te);
    } else {
      return (ContainerSagMill<E>) new Normal(playerInv, (TileSagMill.Normal) te);
    }
  }

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 0;
  public static int NUM_RECIPE_SLOT = 1;
  public static int FIRST_INVENTORY_SLOT = 2 + 4 + 1; // input + output + upgrade
  public static int NUM_INVENTORY_SLOT = 4 * 9;

  public ContainerSagMill(@Nonnull InventoryPlayer playerInv, @Nonnull E te) {
    super(playerInv, te);
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    addSlotToContainer(new InventorySlot(getInv(), 0, 80, 12));
    addSlotToContainer(new InventorySlot(getInv(), 1, 122, 23) {
      @Override
      @SideOnly(Side.CLIENT)
      public boolean isEnabled() {
        return getTe() instanceof TileSagMill.Normal;
      }
    });
    addSlotToContainer(new Slot(getInv(), 2, 49, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 3, 70, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 4, 91, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
    addSlotToContainer(new Slot(getInv(), 5, 112, 59) {
      @Override
      public boolean isItemValid(@Nonnull ItemStack par1ItemStack) {
        return false;
      }
    });
  }

}
