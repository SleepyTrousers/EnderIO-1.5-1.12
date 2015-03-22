package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.util.ItemUtil;
import java.awt.Point;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class InventoryPanelContainer extends AbstractMachineContainer {

  private static final int CRAFTING_GRID_X = 7;
  private static final int CRAFTING_GRID_Y = 16;

  private static final int RETURN_INV_X = 7;
  private static final int RETURN_INV_Y = 82;

  @SuppressWarnings("LeakingThisInConstructor")
  public InventoryPanelContainer(InventoryPlayer playerInv, TileInventoryPanel te) {
    super(playerInv, te);
    te.eventHandler = this;
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    addSlotToContainer(new SlotCrafting(playerInv.player, tileEntity, tileEntity, 9, CRAFTING_GRID_X+59, CRAFTING_GRID_Y+18) {
      @Override
      public void onPickupFromSlot(EntityPlayer player, ItemStack p_82870_2_) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, p_82870_2_, tileEntity);
        for (int i = 0; i < 9; i++) {
          ItemStack itemstack = tileEntity.getStackInSlot(i);
          if(itemstack == null)
            continue;

          tileEntity.decrStackSize(i, 1);
          if(!itemstack.getItem().hasContainerItem(itemstack))
            continue;

          ItemStack containerIS = itemstack.getItem().getContainerItem(itemstack);
          if(containerIS != null && containerIS.isItemStackDamageable() && containerIS.getItemDamage() > containerIS.getMaxDamage()) {
            MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, containerIS));
          } else {
            if(itemstack.getItem().doesContainerItemLeaveCraftingGrid(itemstack)) {
              if(ItemUtil.doInsertItem(tileEntity, 10, 20, itemstack) > 0)
                continue;
              if(player.inventory.addItemStackToInventory(containerIS))
                continue;
            }
            if(tileEntity.getStackInSlot(i) == null) {
              tileEntity.setInventorySlotContents(i, containerIS);
            } else {
              player.dropPlayerItemWithRandomChoice(containerIS, false);
            }
          }
        }
      }
    });

    for(int y=0,i=0 ; y<3 ; y++) {
      for(int x=0 ; x<3 ; x++,i++) {
        addSlotToContainer(new Slot(tileEntity, i, CRAFTING_GRID_X+x*18, CRAFTING_GRID_Y+y*18));
      }
    }

    for(int y=0,i=10 ; y<2 ; y++) {
      for(int x=0 ; x<5 ; x++,i++) {
        addSlotToContainer(new Slot(tileEntity, i, RETURN_INV_X+x*18, RETURN_INV_Y+y*18));
      }
    }
  }

  @Override
  public Point getPlayerInventoryOffset() {
    return new Point(39, 130);
  }

  @Override
  public void onContainerClosed(EntityPlayer player) {
    super.onContainerClosed(player);
    if(!tileEntity.getWorldObj().isRemote) {
      ((TileInventoryPanel)tileEntity).eventHandler = null;
    }
  }

  @Override
  public void onCraftMatrixChanged(IInventory inv) {
    InventoryCrafting tmp = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(EntityPlayer ep) {
        return false;
      }
    }, 3, 3);

    for(int i=0 ; i<9 ; i++) {
      tmp.setInventorySlotContents(i, tileEntity.getStackInSlot(i));
    }

    tileEntity.setInventorySlotContents(9, CraftingManager.getInstance().findMatchingRecipe(tmp, tileEntity.getWorldObj()));
  }

}
