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

  public void connectionModeChanged(ItemConduit itemConduit, ConnectionMode mode) {
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

    void onTick() {
      if(!canExtract()) {
        return;
      }
      if(sidedInv != null) {
        trasnferItemsSided();
      } else {
        tranfserItems();
      }

    }

    private void tranfserItems() {
      int size = inv.getSizeInventory();
      int numSlots = inv.getSizeInventory();
      ItemStack extractItem = null;

      int slot = -1;
      for (int i = 0; i < numSlots && extractItem == null; i++) {
        ItemStack item = inv.getStackInSlot(i);
        if(item != null) {
          extractItem = item.copy();
          slot = i;
        }
      }

      int maxExtracted = con.getMaximumExtracted(slot);
      if(maxExtracted > 0) {
        doTranfser(extractItem, slot, maxExtracted);
      }
    }

    private void trasnferItemsSided() {

      int size = sidedInv.getSizeInventory();
      int[] slotIndices = sidedInv.getAccessibleSlotsFromSide(inventorySide);
      ItemStack extractItem = null;

      int slot = -1;
      for (int i = 0; i < slotIndices.length && extractItem == null; i++) {
        ItemStack item = sidedInv.getStackInSlot(i);
        if(item != null) {
          extractItem = item.copy();
          slot = i;
          if(!sidedInv.canExtractItem(i, extractItem, inventorySide)) {
            extractItem = null;
          }
        }
      }

      int maxExtracted = con.getMaximumExtracted(slot);
      if(maxExtracted > 0) {
        doTranfser(extractItem, slot, maxExtracted);
      }

    }

    private void doTranfser(ItemStack extractedItem, int slot, int maxExtract) {
      if(extractedItem != null) {
        ItemStack toExtract = extractedItem.copy();
        toExtract.stackSize = Math.min(maxExtract, toExtract.stackSize);
        int numInserted = insertIntoTargets(toExtract, slot);

        toExtract.stackSize = extractedItem.stackSize - numInserted;
        if(toExtract.stackSize > 0) {
          inv.setInventorySlotContents(slot, toExtract);
        } else {
          inv.setInventorySlotContents(slot, null);
        }
        con.itemsExtracted(numInserted, slot);
      }
    }

    private int insertIntoTargets(ItemStack toExtract, int slot) {
      if(toExtract == null) {
        return 0;
      }

      int totalToInsert = toExtract.stackSize;
      int leftToInsert = totalToInsert;
      for (Target target : sendPriority) {
        int inserted = target.inv.insertItem(toExtract);
        if(inserted > 0) {
          toExtract.stackSize -= inserted;
          if(toExtract.stackSize > 0) {
            inv.setInventorySlotContents(slot, toExtract);
          } else {
            inv.setInventorySlotContents(slot, null);
          }
          leftToInsert -= inserted;
        }
        if(leftToInsert <= 0) {
          return totalToInsert;
        }
      }
      return totalToInsert - leftToInsert;
    }

    private int insertItem(ItemStack item) {
      if(sidedInv != null) {
        return doInsertItemSided(item);
      }
      return doInsertItem(item);
    }

    private int doInsertItem(ItemStack item) {
      if(!canInsert() || item == null) {
        return 0;

      }
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
          if(contents.isItemEqual(item)) {
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
      for (int slot = 0; slot < inv.getSizeInventory() && numToInsert > 0; slot++) {
        if(sidedInv.canInsertItem(slot, item, inventorySide)) {
          ItemStack contents = inv.getStackInSlot(slot);
          ItemStack toInsert = item.copy();
          toInsert.stackSize = Math.min(toInsert.stackSize, inv.getInventoryStackLimit());
          toInsert.stackSize = Math.min(toInsert.stackSize, numToInsert);
          int inserted = 0;
          if(contents == null) {
            inserted = toInsert.stackSize;
          } else {
            if(contents.isItemEqual(item)) {
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
        if(other != this && other.canInsert()) {
          sendPriority.add(new Target(other, location.distanceSquared(other.location)));
        }
      }
      Collections.sort(sendPriority);
    }

  }

  class Target implements Comparable<Target> {
    NetworkedInventory inv;
    int distance;

    Target(NetworkedInventory inv, int distance) {
      this.inv = inv;
      this.distance = distance;
    }

    @Override
    public int compareTo(Target o) {
      return compare(distance, o.distance);
    }

  }

}
