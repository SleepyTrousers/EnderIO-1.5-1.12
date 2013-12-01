package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit> {

  private long timeAtLastApply;

  private final List<NetworkedInventory> inventories = new ArrayList<ItemConduitNetwork.NetworkedInventory>();
  private final Map<BlockCoord, NetworkedInventory> invMap = new HashMap<BlockCoord, ItemConduitNetwork.NetworkedInventory>();

  private boolean requiresSort = true;

  @Override
  public Class<? extends IItemConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public void addConduit(IItemConduit con) {
    super.addConduit(con);

    TileEntity te = con.getBundle().getEntity();
    if(te != null) {
      for (ForgeDirection direction : con.getExternalConnections()) {
        IInventory extCon = con.getExternalInventory(direction);
        if(extCon != null) {
          inventoryAdded(con, direction, te.xCoord + direction.offsetX, te.yCoord + direction.offsetY, te.zCoord + direction.offsetZ, extCon);
        }
      }
    }
  }

  public void inventoryAdded(IItemConduit itemConduit, ForgeDirection direction, int x, int y, int z, IInventory externalInventory) {
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory inv = new NetworkedInventory(externalInventory, itemConduit, direction, bc);
    inventories.add(inv);
    invMap.put(bc, inv);
    requiresSort = true;
  }

  public void inventoryRemoved(ItemConduit itemConduit, int x, int y, int z) {
    BlockCoord bc = new BlockCoord(x, y, z);
    NetworkedInventory inv = invMap.remove(bc);
    if(inv != null) {
      inventories.remove(inv);
    }
    requiresSort = true;
  }

  public void routesChanged() {
    requiresSort = true;
  }

  private boolean isRemote(ItemConduit itemConduit) {
    World world = itemConduit.getBundle().getEntity().worldObj;
    if(world != null && world.isRemote) {
      return true;
    }
    return false;
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().worldObj;
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      doTick();
    }
  }

  private void doTick() {
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        ni.updateInsertOrder();
      }
      ni.onTick();
    }
    requiresSort = false;
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  class NetworkedInventory {

    IInventory inv;
    ISidedInventory sidedInv;
    IItemConduit con;
    ForgeDirection conDir;
    BlockCoord location;
    int inventorySide;

    boolean allowSelfFeed = false;

    List<Target> sendPriority = new ArrayList<Target>();

    NetworkedInventory(IInventory inv, IItemConduit con, ForgeDirection conDir, BlockCoord location) {
      this.inv = inv;
      if(inv instanceof ISidedInventory) {
        sidedInv = (ISidedInventory) inv;
      }
      this.con = con;
      this.conDir = conDir;
      this.location = location;
      inventorySide = conDir.getOpposite().ordinal();
    }

    boolean canExtract() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.INPUT || mode == ConnectionMode.IN_OUT;
    }

    boolean canInsert() {
      ConnectionMode mode = con.getConectionMode(conDir);
      return mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
    }

    boolean isSticky() {
      return con.getOutputFilter(conDir).isValid() && con.getOutputFilter(conDir).isSticky();
    }

    void onTick() {
      if(!canExtract()) {
        return;
      }
      if(sidedInv != null) {
        transferItemsSided();
      } else {
        tranfserItems();
      }

    }

    private void tranfserItems() {
      int size = inv.getSizeInventory();
      int numSlots = inv.getSizeInventory();
      ItemStack extractItem = null;

      int slot = -1;
      for (int i = 0; i < numSlots; i++) {
        ItemStack item = inv.getStackInSlot(i);
        if(canExtractItem(item)) {
          extractItem = item.copy();
          slot = i;
          int maxExtracted = con.getMaximumExtracted();
          if(maxExtracted > 0) {
            if(doTranfser(extractItem, slot, maxExtracted)) {
              return;
            }

          }
        }
      }

    }

    private void transferItemsSided() {

      int size = sidedInv.getSizeInventory();
      int[] slotIndices = sidedInv.getAccessibleSlotsFromSide(inventorySide);
      ItemStack extractItem = null;

      int slot = -1;
      for (int i = 0; i < slotIndices.length; i++) {
        slot = slotIndices[i];
        ItemStack item = sidedInv.getStackInSlot(slot);
        if(canExtractItem(item)) {
          extractItem = item.copy();
          if(sidedInv.canExtractItem(i, extractItem, inventorySide)) {
            int maxExtracted = con.getMaximumExtracted();
            if(maxExtracted > 0) {
              if(doTranfser(extractItem, slot, maxExtracted)) {
                return;
              }
            }
          }
        }
      }
    }

    private boolean canExtractItem(ItemStack itemStack) {
      if(itemStack == null) {
        return false;
      }

      if(!con.isExtractionRedstoneConditionMet(conDir)) {
        return false;
      }
      ItemFilter filter = con.getInputFilter(conDir);
      if(filter == null) {
        return true;
      }
      return filter.doesItemPassFilter(itemStack);
    }

    private boolean doTranfser(ItemStack extractedItem, int slot, int maxExtract) {
      if(extractedItem == null) {
        return false;
      }
      ItemStack toExtract = extractedItem.copy();
      toExtract.stackSize = Math.min(maxExtract, toExtract.stackSize);
      int numInserted = insertIntoTargets(toExtract, slot);
      if(numInserted <= 0) {
        return false;
      }

      ItemStack curStack = inv.getStackInSlot(slot);
      if(curStack != null) {
        curStack = curStack.copy();
        curStack.stackSize -= numInserted;
        if(curStack.stackSize > 0) {
          inv.setInventorySlotContents(slot, curStack);
        } else {
          inv.setInventorySlotContents(slot, null);
        }
      }
      con.itemsExtracted(numInserted, slot);
      return true;

    }

    private int insertIntoTargets(ItemStack toExtract, int slot) {
      if(toExtract == null) {
        return 0;
      }

      int totalToInsert = toExtract.stackSize;
      int leftToInsert = totalToInsert;
      boolean matchedStickyInput = false;

      for (Target target : sendPriority) {
        if(target.stickyInput && !matchedStickyInput) {
          ItemFilter of = target.inv.con.getOutputFilter(target.inv.conDir);
          matchedStickyInput = of.isValid() && of.doesItemPassFilter(toExtract);
        }
        if(target.stickyInput || !matchedStickyInput) {
          int inserted = target.inv.insertItem(toExtract);
          if(inserted > 0) {
            toExtract.stackSize -= inserted;
            leftToInsert -= inserted;
          }
          if(leftToInsert <= 0) {
            return totalToInsert;
          }
        }
      }
      return totalToInsert - leftToInsert;
    }

    private int insertItem(ItemStack item) {
      if(!canInsert() || item == null) {
        return 0;
      }
      ItemFilter filter = con.getOutputFilter(conDir);
      if(filter != null) {
        if(!filter.doesItemPassFilter(item)) {
          return 0;
        }
      }

      if(sidedInv != null) {
        return doInsertItemSided(item);
      }
      return doInsertItem(item);
    }

    private int doInsertItem(ItemStack item) {
      int numInserted = 0;
      int numToInsert = item.stackSize;
      for (int slot = 0; slot < inv.getSizeInventory() && numToInsert > 0; slot++) {
        ItemStack contents = inv.getStackInSlot(slot);
        ItemStack toInsert = item.copy();
        toInsert.stackSize = Math.min(toInsert.stackSize, inv.getInventoryStackLimit());
        toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
        int inserted = 0;
        if(contents == null) {
          inserted = toInsert.stackSize;
        } else {
          if(contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
            int space = inv.getInventoryStackLimit() - contents.stackSize;
            space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
            inserted += Math.min(space, toInsert.stackSize);
            toInsert.stackSize = contents.stackSize + inserted;
          } else {
            toInsert.stackSize = 0;
          }
        }

        if(inserted > 0) {
          numInserted += inserted;
          numToInsert -= inserted;
          inv.setInventorySlotContents(slot, toInsert);
        }
      }
      return numInserted;

    }

    private int doInsertItemSided(ItemStack item) {

      int numInserted = 0;
      int numToInsert = item.stackSize;
      int[] slots = sidedInv.getAccessibleSlotsFromSide(inventorySide);
      for (int i = 0; i < slots.length && numToInsert > 0; i++) {
        int slot = slots[i];
        if(sidedInv.canInsertItem(slot, item, inventorySide)) {
          ItemStack contents = inv.getStackInSlot(slot);
          ItemStack toInsert = item.copy();
          toInsert.stackSize = Math.min(toInsert.stackSize, inv.getInventoryStackLimit());
          toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
          int inserted = 0;
          if(contents == null) {
            inserted = toInsert.stackSize;
          } else {
            if(contents.isItemEqual(item) && ItemStack.areItemStackTagsEqual(contents, item)) {
              int space = inv.getInventoryStackLimit() - contents.stackSize;
              space = Math.min(space, contents.getMaxStackSize() - contents.stackSize);
              inserted += Math.min(space, toInsert.stackSize);
              toInsert.stackSize = contents.stackSize + inserted;
            } else {
              toInsert.stackSize = 0;
            }
          }

          if(inserted > 0) {
            numInserted += inserted;
            numToInsert -= inserted;
            inv.setInventorySlotContents(slot, toInsert);
          }
        }
      }
      return numInserted;
    }

    void updateInsertOrder() {
      sendPriority.clear();
      if(!canExtract()) {
        return;
      }
      for (NetworkedInventory other : inventories) {
        if((allowSelfFeed || (other != this)) && other.canInsert()) {
          sendPriority.add(new Target(other, location.distanceSquared(other.location), other.isSticky()));
        }
      }
      Collections.sort(sendPriority);
    }

  }

  class Target implements Comparable<Target> {
    NetworkedInventory inv;
    int distance;
    boolean stickyInput;

    Target(NetworkedInventory inv, int distance, boolean stickyInput) {
      this.inv = inv;
      this.distance = distance;
      this.stickyInput = stickyInput;
    }

    @Override
    public int compareTo(Target o) {
      if(stickyInput && !o.stickyInput) {
        return -1;
      }
      if(!stickyInput && o.stickyInput) {
        return 1;
      }
      return compare(distance, o.distance);
    }

  }

}
