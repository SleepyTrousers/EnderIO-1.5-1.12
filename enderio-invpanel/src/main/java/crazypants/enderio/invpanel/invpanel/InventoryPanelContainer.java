package crazypants.enderio.invpanel.invpanel;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

import com.enderio.core.client.gui.GhostSlotHandler;
import com.enderio.core.client.gui.widget.GhostBackgroundItemSlot;
import com.enderio.core.common.util.ItemUtil;

import crazypants.enderio.base.invpanel.database.IChangeLog;
import crazypants.enderio.base.invpanel.database.IInventoryDatabaseServer;
import crazypants.enderio.base.invpanel.database.IServerItemEntry;
import crazypants.enderio.base.machine.gui.AbstractMachineContainer;
import crazypants.enderio.invpanel.network.PacketHandler;
import crazypants.enderio.invpanel.network.PacketItemList;
import crazypants.enderio.invpanel.network.PacketMoveItems;
import crazypants.enderio.invpanel.remote.ItemRemoteInvAccess;
import crazypants.enderio.invpanel.util.SlotCraftingWrapper;
import crazypants.enderio.invpanel.util.StoredCraftingRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.world.World;

import static crazypants.enderio.base.init.ModObject.itemBasicItemFilter;

public class InventoryPanelContainer extends AbstractMachineContainer<TileInventoryPanel> implements IChangeLog {

  // JEI wants this data without giving us a chance to instantiate a container
  public static int FIRST_RECIPE_SLOT = 1;
  public static int NUM_RECIPE_SLOT = 9;
  public static int FIRST_INVENTORY_SLOT = 11;
  public static int NUM_INVENTORY_SLOT = 10 + 4 * 9; // return area + player inventory

  /*
   * Slots: slotCraftResult=0 firstSlotCraftingGrid=1 endSlotCraftingGrid=10 firstSlotReturn=11 endSlotReturn=21 startHotBarSlot=48 endHotBarSlot=57
   * startPlayerSlot=21 endPlayerSlot=48
   */

  public static final int CRAFTING_GRID_X = 7;
  public static final int CRAFTING_GRID_Y = 16;

  public static final int RETURN_INV_X = 7;
  public static final int RETURN_INV_Y = 82;

  public static final int FILTER_SLOT_X = 233;
  public static final int FILTER_SLOT_Y = 7;

  private final HashSet<IServerItemEntry> changedItems;

  private Slot slotFilter;

  private int slotCraftResult;
  private int firstSlotReturn;
  private int endSlotReturn;
  private int firstSlotCraftingGrid;
  private int endSlotCraftingGrid;

  private boolean updateReturnAreaSlots;
  private boolean storedRecipeExists;

  private @Nonnull final World playerWorld;

  private SlotCraftingWrapper slotCraft;

  public InventoryPanelContainer(@Nonnull InventoryPlayer playerInv, @Nonnull TileInventoryPanel te) {
    super(playerInv, te);
    te.eventHandler = this;
    playerWorld = playerInv.player.world;

    if (!te.hasWorld() || te.getWorld().isRemote) {
      changedItems = null;
    } else {
      changedItems = new HashSet<>();
    }

    slotCraft.inventory = te;
  }

  @Override
  protected void addMachineSlots(@Nonnull InventoryPlayer playerInv) {
    slotCraftResult = inventorySlots.size();
    addSlotToContainer(slotCraft = new SlotCraftingWrapper(playerInv.player, new InventoryCraftingWrapper(getInv(), this, 3, 3), getInv(),
        TileInventoryPanel.SLOT_CRAFTING_RESULT, CRAFTING_GRID_X + 59, CRAFTING_GRID_Y + 18));

    firstSlotCraftingGrid = inventorySlots.size();
    for (int y = 0, i = TileInventoryPanel.SLOT_CRAFTING_START; y < 3; y++) {
      for (int x = 0; x < 3; x++, i++) {
        addSlotToContainer(new Slot(getInv(), i, CRAFTING_GRID_X + x * 18, CRAFTING_GRID_Y + y * 18));
      }
    }
    endSlotCraftingGrid = inventorySlots.size();

    slotFilter = addSlotToContainer(new Slot(getInv(), TileInventoryPanel.SLOT_VIEW_FILTER, FILTER_SLOT_X, FILTER_SLOT_Y) {
      @Override
      public int getSlotStackLimit() {
        return 1;
      }

    });

    firstSlotReturn = inventorySlots.size();
    for (int y = 0, i = TileInventoryPanel.SLOT_RETURN_START; y < 3; y++) {
      for (int x = 0; x < 5; x++, i++) {
        addSlotToContainer(new Slot(getInv(), i, RETURN_INV_X + x * 18, RETURN_INV_Y + y * 18));
      }
    }
    endSlotReturn = inventorySlots.size();
  }

  public void createGhostSlots(GhostSlotHandler slots) {
    slots.add(new GhostBackgroundItemSlot(itemBasicItemFilter.getItemNN(), FILTER_SLOT_X, FILTER_SLOT_Y));
  }

  @Override
  @Nonnull
  public Point getPlayerInventoryOffset() {
    return new Point(39, 148);
  }

  @Override
  public void onContainerClosed(@Nonnull EntityPlayer player) {
    super.onContainerClosed(player);
    if (getTe().hasWorld() && !getTe().getWorld().isRemote) {
      getTe().eventHandler = null;
    }
    removeChangeLog();
  }

  public Slot getSlotFilter() {
    return slotFilter;
  }

  public List<Slot> getCraftingGridSlots() {
    return inventorySlots.subList(firstSlotCraftingGrid, endSlotCraftingGrid);
  }

  public List<Slot> getReturnAreaSlots() {
    return inventorySlots.subList(firstSlotReturn, endSlotReturn);
  }

  public List<Slot> getPlayerInventorySlots() {
    return inventorySlots.subList(startPlayerSlot, endPlayerSlot);
  }

  public List<Slot> getPlayerHotbarSlots() {
    return inventorySlots.subList(startHotBarSlot, endHotBarSlot);
  }

  private void removeChangeLog() {
    if (changedItems != null) {
      IInventoryDatabaseServer db = getTe().getDatabaseServer();
      if (db != null) {
        db.removeChangeLog(this);
      }
    }
  }

  @Override
  public void removeListener(@Nonnull IContainerListener crafting) {
    super.removeListener(crafting);
    removeChangeLog();
  }

  @Override
  public void addListener(@Nonnull IContainerListener crafting) {
    if (changedItems != null) {
      sendChangeLog();
    }
    super.addListener(crafting);
    if (changedItems != null) {
      IInventoryDatabaseServer db = getTe().getDatabaseServer();
      if (db != null) {
        db.addChangeLog(this);
        if (crafting instanceof EntityPlayerMP) {
          try {
            byte[] compressed = db.compressItemList();
            PacketItemList pil = new PacketItemList(windowId, db.getGeneration(), compressed);
            PacketHandler.sendTo(pil, (EntityPlayerMP) crafting);
          } catch (IOException ex) {
            Logger.getLogger(InventoryPanelContainer.class.getName()).log(Level.SEVERE, "Exception while compressing item list", ex);
          }
        }
      }
    }
  }

  @Override
  public void onCraftMatrixChanged(@Nonnull IInventory inv) {
    InventoryCrafting tmp = new InventoryCrafting(new Container() {
      @Override
      public boolean canInteractWith(@Nonnull EntityPlayer ep) {
        return false;
      }
    }, 3, 3);

    for (int i = 0; i < 9; i++) {
      tmp.setInventorySlotContents(i, getTe().getStackInSlot(i));
    }

    getTe().setInventorySlotContents(9, CraftingManager.findMatchingResult(tmp, playerWorld));

    checkCraftingRecipes();
  }

  public void checkCraftingRecipes() {
    storedRecipeExists = false;
    int storedCraftingRecipes = getTe().getStoredCraftingRecipes();
    if (hasCraftingRecipe() && storedCraftingRecipes > 0) {
      List<Slot> craftingGrid = getCraftingGridSlots();
      for (int idx = 0; idx < storedCraftingRecipes; idx++) {
        StoredCraftingRecipe recipe = getTe().getStoredCraftingRecipe(idx);
        if (recipe != null && recipe.isEqual(craftingGrid)) {
          storedRecipeExists = true;
          break;
        }
      }
    }
  }

  @Override
  public boolean canMergeSlot(@Nonnull ItemStack par1, @Nonnull Slot slot) {
    return !(slot instanceof SlotCrafting) && super.canMergeSlot(par1, slot);
  }

  public boolean clearCraftingGrid() {
    boolean cleared = true;
    for (Slot slot : getCraftingGridSlots()) {
      if (slot.getHasStack()) {
        moveItemsToReturnArea(slot.slotNumber);
        if (slot.getHasStack()) {
          cleared = false;
        }
      }
    }
    return cleared;
  }

  public boolean hasCraftingRecipe() {
    return getSlot(slotCraftResult).getHasStack();
  }

  public boolean hasNewCraftingRecipe() {
    return hasCraftingRecipe() && !storedRecipeExists;
  }

  @Override
  @Nonnull
  protected List<SlotRange> getTargetSlotsForTransfer(int slotIndex, @Nonnull Slot slot) {
    if ((slotIndex == slotCraftResult) || (slotIndex >= firstSlotReturn && slotIndex < endSlotReturn)) {
      return Collections.singletonList(getPlayerInventorySlotRange(true));
    }
    if (slotIndex >= firstSlotCraftingGrid && slotIndex < endSlotCraftingGrid) {
      ArrayList<SlotRange> res = new ArrayList<SlotRange>();
      res.add(new SlotRange(firstSlotReturn, endSlotReturn, false));
      res.add(getPlayerInventorySlotRange(false));
      return res;
    }
    if (slotIndex >= startPlayerSlot) {
      return Collections.singletonList(new SlotRange(firstSlotReturn, endSlotReturn, false));
    }
    return Collections.emptyList();
  }

  @Override
  protected boolean mergeItemStack(@Nonnull ItemStack par1ItemStack, int fromIndex, int toIndex, boolean reversOrder) {
    if (!super.mergeItemStack(par1ItemStack, fromIndex, toIndex, reversOrder)) {
      return false;
    }
    if (fromIndex < endSlotReturn && toIndex > firstSlotReturn) {
      updateReturnAreaSlots = true;
    }
    return true;
  }

  @Override
  public void detectAndSendChanges() {
    if (updateReturnAreaSlots) {
      updateReturnAreaSlots = false;
      sendReturnAreaSlots();
    }
    super.detectAndSendChanges();
  }

  private void sendReturnAreaSlots() {
    for (int slotIdx = firstSlotReturn; slotIdx < endSlotReturn; slotIdx++) {
      ItemStack stack = inventorySlots.get(slotIdx).getStack();
      if (!stack.isEmpty()) {
        stack = stack.copy();
      }
      inventoryItemStacks.set(slotIdx, stack);
      for (IContainerListener crafter : this.listeners) {
        crafter.sendSlotContents(this, slotIdx, stack);
      }
    }
  }

  @Override
  public void entryChanged(IServerItemEntry entry) {
    changedItems.add(entry);
  }

  @Override
  public void databaseReset() {
    changedItems.clear();
  }

  @Override
  public void sendChangeLog() {
    if (!changedItems.isEmpty() && !listeners.isEmpty()) {
      IInventoryDatabaseServer db = getTe().getDatabaseServer();
      if (db != null) {
        try {
          byte[] compressed = db.compressChangedItems(changedItems);
          PacketItemList pil = new PacketItemList(windowId, db.getGeneration(), compressed);
          for (Object crafting : listeners) {
            if (crafting instanceof EntityPlayerMP) {
              PacketHandler.sendTo(pil, (EntityPlayerMP) crafting);
            }
          }
        } catch (IOException ex) {
          Logger.getLogger(InventoryPanelContainer.class.getName()).log(Level.SEVERE, "Exception while compressing changed items", ex);
        }
      }
    }
    changedItems.clear();
  }

  public int getSlotIndex(@Nonnull IInventory inv, int index) {
    for (int i = 0; i < inventorySlots.size(); i++) {
      Slot slot = inventorySlots.get(i);
      if (slot.isHere(inv, index)) {
        return i;
      }
    }
    return -1;
  }

  public void executeFetchItems(EntityPlayerMP player, int generation, int dbID, int targetSlot, int count) {
    IInventoryDatabaseServer db = te.getDatabaseServer();
    if (db == null || db.getGeneration() != generation || !db.isCurrent()) {
      return;
    }
    IServerItemEntry entry = db.getExistingItem(dbID);
    if (entry != null) {
      ItemStack targetStack;
      Slot slot;
      int maxStackSize;

      if (targetSlot < 0) {
        slot = null;
        targetStack = player.inventory.getItemStack();
        maxStackSize = player.inventory.getInventoryStackLimit();
      } else {
        slot = getSlot(targetSlot);
        targetStack = slot.getStack();
        maxStackSize = slot.getSlotStackLimit();
      }

      ItemStack tmpStack = new ItemStack(entry.getItem(), 1, entry.getMeta());
      tmpStack.setTagCompound(entry.getNbt());
      maxStackSize = Math.min(maxStackSize, tmpStack.getMaxStackSize());

      if (!targetStack.isEmpty()) {
        if (!ItemUtil.areStackMergable(tmpStack, targetStack)) {
          return;
        }
        count = Math.min(count, maxStackSize - targetStack.getCount());
      } else {
        count = Math.min(count, maxStackSize);
      }

      if (count > 0) {
        int extracted = db.extractItems(entry, count, te);
        if (extracted > 0) {
          if (!targetStack.isEmpty()) {
            targetStack.grow(extracted);
          } else {
            targetStack = tmpStack.copy();
            targetStack.setCount(extracted);
          }

          // TODO Debug stuff
          // if (DebugCommand.SERVER.isEnabled(player)) {
          // DebugCommand.SERVER.debug("extracted " + targetStack + " for dbid=" + dbID + " " + entry);
          // }
          // System.out.println("extracted " + targetStack + " for dbid=" + dbID + " " + entry);

          sendChangeLog();

          if (slot != null) {
            slot.putStack(targetStack);
          } else {
            player.inventory.setItemStack(targetStack);
            player.updateHeldItem();
          }
        }
      }
    }
    this.detectAndSendChanges();
  }

  public boolean moveItemsToReturnArea(int fromSlot) {
    return moveItems(fromSlot, firstSlotReturn, endSlotReturn, Short.MAX_VALUE);
  }

  public boolean moveItems(int fromSlot, int toSlotStart, int toSlotEnd, int amount) {
    if (!executeMoveItems(fromSlot, toSlotStart, toSlotEnd, amount)) {
      return false;
    }
    if (!getTe().hasWorld() || getTe().getWorld().isRemote) {
      PacketHandler.INSTANCE.sendToServer(new PacketMoveItems(fromSlot, toSlotStart, toSlotEnd, amount));
    }
    return true;
  }

  public boolean executeMoveItems(int fromSlot, int toSlotStart, int toSlotEnd, int amount) {
    if ((fromSlot >= toSlotStart && fromSlot < toSlotEnd) || toSlotEnd <= toSlotStart || amount <= 0) {
      return false;
    }

    Slot srcSlot = getSlot(fromSlot);
    ItemStack src = srcSlot.getStack();
    if (!src.isEmpty()) {
      ItemStack toMove = src.copy();
      toMove.setCount(Math.min(src.getCount(), amount));
      int remaining = src.getCount() - toMove.getCount();
      if (mergeItemStack(toMove, toSlotStart, toSlotEnd, false)) {
        remaining += toMove.getCount();
        if (remaining == 0) {
          srcSlot.putStack(ItemStack.EMPTY);
        } else {
          src.setCount(remaining);
          srcSlot.onSlotChanged();
        }
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("null")
  @Override
  public boolean canInteractWith(@Nonnull EntityPlayer player) {
    if (super.canInteractWith(player)) {
      return true;
    }
    if ((!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemRemoteInvAccess)) {
      if (!player.getEntityWorld().isRemote) {
        return ((ItemRemoteInvAccess) player.getHeldItemMainhand().getItem()).canInteractWith(player.getHeldItemMainhand(), player);
      } else {
        return true;
      }
    } else if ((!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemRemoteInvAccess)) {
      if (!player.getEntityWorld().isRemote) {
        return ((ItemRemoteInvAccess) player.getHeldItemOffhand().getItem()).canInteractWith(player.getHeldItemOffhand(), player);
      } else {
        return true;
      }
    }
    return false;
  }

  @SuppressWarnings("null")
  public void tick(@Nonnull EntityPlayer player) {
    if (!super.canInteractWith(player)) {
      if ((!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() instanceof ItemRemoteInvAccess)) {
        ((ItemRemoteInvAccess) player.getHeldItemMainhand().getItem()).tick(player.getHeldItemMainhand(), player);
      } else if ((!player.getHeldItemOffhand().isEmpty() && player.getHeldItemOffhand().getItem() instanceof ItemRemoteInvAccess)) {
        ((ItemRemoteInvAccess) player.getHeldItemOffhand().getItem()).tick(player.getHeldItemOffhand(), player);
      }
    }
  }
}
