package crazypants.enderio.machine.invpanel;

import cpw.mods.fml.common.FMLCommonHandler;
import crazypants.enderio.machine.SlotDefinition;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.ItemUtil;
import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class InventoryPanelContainer extends AbstractMachineContainer implements InventoryDatabaseServer.ChangeLog {

  public static final int CRAFTING_GRID_X = 7;
  public static final int CRAFTING_GRID_Y = 16;

  public static final int RETURN_INV_X = 7;
  public static final int RETURN_INV_Y = 82;

  public static final int FILTER_SLOT_X = 233;
  public static final int FILTER_SLOT_Y = 7;

  private final HashSet<InventoryDatabaseServer.ItemEntry> changedItems;

  private Slot slotFilter;

  private int indexCraftingSlot;
  private int indexFilterSlot;
  private int startReturnSlot;
  private int endReturnSlot;

  @SuppressWarnings("LeakingThisInConstructor")
  public InventoryPanelContainer(InventoryPlayer playerInv, TileInventoryPanel te) {
    super(playerInv, te);
    te.eventHandler = this;

    if(te.getWorldObj().isRemote) {
      changedItems = null;
    } else {
      changedItems = new HashSet<InventoryDatabaseServer.ItemEntry>();
    }
  }

  @Override
  protected void addMachineSlots(InventoryPlayer playerInv) {
    indexCraftingSlot = inventorySlots.size();
    addSlotToContainer(new SlotCrafting(playerInv.player, tileEntity, tileEntity,
            TileInventoryPanel.SLOT_CRAFTING_RESULT, CRAFTING_GRID_X+59, CRAFTING_GRID_Y+18) {
      @Override
      public void onPickupFromSlot(EntityPlayer player, ItemStack p_82870_2_) {
        FMLCommonHandler.instance().firePlayerCraftingEvent(player, p_82870_2_, tileEntity);
        for (int i = TileInventoryPanel.SLOT_CRAFTING_START; i < TileInventoryPanel.SLOT_CRAFTING_RESULT; i++) {
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

    for(int y=0,i=TileInventoryPanel.SLOT_CRAFTING_START ; y<3 ; y++) {
      for(int x=0 ; x<3 ; x++,i++) {
        addSlotToContainer(new Slot(tileEntity, i, CRAFTING_GRID_X+x*18, CRAFTING_GRID_Y+y*18));
      }
    }

    indexFilterSlot = inventorySlots.size();
    slotFilter = addSlotToContainer(new Slot(tileEntity, TileInventoryPanel.SLOT_VIEW_FILTER, FILTER_SLOT_X, FILTER_SLOT_Y) {
      @Override
      public int getSlotStackLimit() {
        return 1;
      }
    });

    startReturnSlot = inventorySlots.size();
    for(int y=0,i=TileInventoryPanel.SLOT_RETURN_START ; y<2 ; y++) {
      for(int x=0 ; x<5 ; x++,i++) {
        addSlotToContainer(new Slot(tileEntity, i, RETURN_INV_X+x*18, RETURN_INV_Y+y*18));
      }
    }
    endReturnSlot = inventorySlots.size();
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
    removeChangeLog();
  }

  private TileInventoryPanel getInventoryPanel() {
    return (TileInventoryPanel) tileEntity;
  }

  public Slot getSlotFilter() {
    return slotFilter;
  }

  private void removeChangeLog() {
    if(changedItems != null) {
      getInventoryPanel().getDatabaseServer().removeChangeLog(this);
    }
  }

  @Override
  public void removeCraftingFromCrafters(ICrafting crafting) {
    super.removeCraftingFromCrafters(crafting);
    removeChangeLog();
  }

  @Override
  public void addCraftingToCrafters(ICrafting crafting) {
    if(changedItems != null) {
      sendChangeLog();
    }
    super.addCraftingToCrafters(crafting);
    if(changedItems != null) {
      InventoryDatabaseServer db = getInventoryPanel().getDatabaseServer();
      db.addChangeLog(this);
      if(crafting instanceof EntityPlayerMP) {
        try {
          byte[] compressed = db.compressItemList();
          PacketItemList pil = new PacketItemList(getInventoryPanel(), compressed);
          PacketHandler.sendTo(pil, (EntityPlayerMP)crafting);
        } catch (IOException ex) {
          Logger.getLogger(InventoryPanelContainer.class.getName()).log(Level.SEVERE,
                  "Exception while compressing item list", ex);
        }
      }
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

  @Override
  public boolean func_94530_a(ItemStack par1, Slot slot) {
    return !(slot instanceof SlotCrafting) && super.func_94530_a(par1, slot);
  }

  @Override
  protected List<SlotRange> getTargetSlotsForTransfer(int slotIndex, Slot slot) {
    SlotDefinition slotDef = tileEntity.getSlotDefinition();
    if(slotDef.isInputSlot(slotIndex) || slotDef.isUpgradeSlot(slotIndex)) {
      return Collections.singletonList(getPlayerInventorySlotRange(false));
    }
    if(slotDef.isOutputSlot(slotIndex) || slotIndex == indexCraftingSlot) {
      return Collections.singletonList(getPlayerInventorySlotRange(true));
    }
    if(slotIndex >= startPlayerSlot) {
      ArrayList<SlotRange> res = new ArrayList<SlotRange>();
      res.add(new SlotRange(startReturnSlot, endReturnSlot, false));
      addPlayerSlotRanges(res, slotIndex);
      return res;
    }
    return Collections.emptyList();
  }

  @Override
  public void entryChanged(InventoryDatabaseServer.ItemEntry entry) {
    changedItems.add(entry);
  }

  @Override
  public void databaseReset() {
    changedItems.clear();
  }

  @Override
  public void sendChangeLog() {
    if(!changedItems.isEmpty() && !crafters.isEmpty()) {
      try {
        InventoryDatabaseServer db = getInventoryPanel().getDatabaseServer();
        byte[] compressed = db.compressChangedItems(changedItems);
        PacketItemList pil = new PacketItemList(getInventoryPanel(), compressed);
        for(Object crafting : crafters) {
          if(crafting instanceof EntityPlayerMP) {
            PacketHandler.sendTo(pil, (EntityPlayerMP) crafting);
          }
        }
      } catch (IOException ex) {
        Logger.getLogger(InventoryPanelContainer.class.getName()).log(Level.SEVERE,
                "Exception while compressing changed items", ex);
      }
    }
    changedItems.clear();
  }

  public int getSlotIndex(IInventory inv, int index) {
    for (int i = 0; i < inventorySlots.size(); i++) {
      Slot slot = (Slot)inventorySlots.get(i);
      if (slot.isSlotInInventory(inv, index)) {
        return i;
      }
    }
    return -1;
  }

  public void executeFetchItems(EntityPlayerMP player, int dbID, int targetSlot, int count) {
    InventoryDatabaseServer db = getInventoryPanel().getDatabaseServer();
    InventoryDatabaseServer.ItemEntry entry = db.getExistingItem(dbID);
    if(entry != null) {
      ItemStack targetStack;
      Slot slot;
      int maxStackSize;

      if(targetSlot < 0) {
        slot = null;
        targetStack = player.inventory.getItemStack();
        maxStackSize = player.inventory.getInventoryStackLimit();
      } else {
        slot = getSlot(targetSlot);
        targetStack = slot.getStack();
        maxStackSize = slot.getSlotStackLimit();
      }

      //System.out.println("executeFetchItems targetSlot="+targetSlot+" slot="+slot+" maxStackSize="+maxStackSize+" count="+count);

      ItemStack tmpStack = new ItemStack(entry.getItem(), 0, entry.meta);
      tmpStack.stackTagCompound = entry.nbt;
      maxStackSize = Math.min(maxStackSize, tmpStack.getMaxStackSize());

      if(targetStack != null && targetStack.stackSize > 0) {
        if(!ItemUtil.areStackMergable(tmpStack, targetStack)) {
          return;
        }
      } else {
        targetStack = tmpStack.copy();
      }

      count = Math.min(count, maxStackSize - targetStack.stackSize);
      if(count > 0) {
        int extracted = entry.extractItems(db, count);
        if(extracted > 0) {
          targetStack.stackSize += extracted;

          sendChangeLog();

          if(slot != null) {
            slot.putStack(targetStack);
          } else {
            player.inventory.setItemStack(targetStack);
            player.updateHeldItem();
          }
        }
      }
    }
  }
}
