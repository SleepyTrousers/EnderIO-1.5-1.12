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
import crazypants.util.ItemUtil;

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

  public ItemStack sendItems(ItemConduit itemConduit, ItemStack item, ForgeDirection side) {
    BlockCoord loc = itemConduit.getLocation().getLocation(side);
    NetworkedInventory inv = invMap.get(loc);
    if(inv == null) {
      return item;
    }
    int numInserted = inv.insertIntoTargets(item);
    if(numInserted >= item.stackSize) {
      return null;
    }
    ItemStack result = item.copy();
    result.stackSize -= numInserted;
    return result;
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
      doTick(world.getTotalWorldTime());
    }
  }

  private void doTick(long tick) {
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        ni.updateInsertOrder();
      }
      ni.onTick(tick);
    }
    requiresSort = false;
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  private static int MAX_SLOT_CHECK_PER_TICK = 64;

  class NetworkedInventory {

    IInventory inv;
    ISidedInventory sidedInv;
    IItemConduit con;
    ForgeDirection conDir;
    BlockCoord location;
    int inventorySide;

    boolean allowSelfFeed = false;

    List<Target> sendPriority = new ArrayList<Target>();

    private int extractFromSlot = -1;

    int tickDeficit;

    NetworkedInventory(IInventory inv, IItemConduit con, ForgeDirection conDir, BlockCoord location) {
      this.inv = inv;

      inventorySide = conDir.getOpposite().ordinal();

      this.con = con;
      this.conDir = conDir;
      this.location = location;

      if(inv instanceof ISidedInventory) {
        sidedInv = (ISidedInventory) inv;
      }

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

    public void onTick(long tick) {
      int transfered;
      if(tickDeficit > 0 || !canExtract() || !con.isExtractionRedstoneConditionMet(conDir)) {
        //do nothing
      } else if(sidedInv != null) {
        transferItemsSided();
      } else {
        transferItems();
      }

      tickDeficit--;
      if(tickDeficit < -1) {
        //Sleep for a second before checking again.
        tickDeficit = 20;
      }
    }

    private boolean canExtractThisTick(long tick) {
      if(!con.isExtractionRedstoneConditionMet(conDir)) {
        return false;
      }
      return true;
    }

    private int nextSlot(int numSlots) {
      ++extractFromSlot;
      if(extractFromSlot >= numSlots || extractFromSlot < 0) {
        extractFromSlot = 0;
      }
      return extractFromSlot;
    }

    private boolean transferItems() {
      int numSlots = inv.getSizeInventory();
      ItemStack extractItem = null;

      int maxExtracted = con.getMaximumExtracted();
      int slot = -1;
      int slotChecksPerTick = Math.min(numSlots, MAX_SLOT_CHECK_PER_TICK);
      for (int i = 0; i < slotChecksPerTick; i++) {
        int index = nextSlot(numSlots);
        ItemStack item = inv.getStackInSlot(index);
        if(canExtractItem(item)) {
          extractItem = item.copy();
          slot = index;
          if(doTransfer(extractItem, slot, maxExtracted)) {
            setNextStartingSlot(slot - 1);
            return true;
          }
        }
      }
      return false;
    }

    private void setNextStartingSlot(int slot) {
      extractFromSlot = slot;
      extractFromSlot--;
    }

    private boolean transferItemsSided() {

      int[] slotIndices = sidedInv.getAccessibleSlotsFromSide(inventorySide);
      int numSlots = slotIndices.length;
      ItemStack extractItem = null;
      int maxExtracted = con.getMaximumExtracted();

      int slot = -1;
      int slotChecksPerTick = Math.min(numSlots, MAX_SLOT_CHECK_PER_TICK);
      for (int i = 0; i < slotChecksPerTick; i++) {
        int index = nextSlot(numSlots);
        slot = slotIndices[index];
        ItemStack item = sidedInv.getStackInSlot(slot);
        if(canExtractItem(item)) {
          extractItem = item.copy();
          if(sidedInv.canExtractItem(index, extractItem, inventorySide)) {
            if(doTransfer(extractItem, slot, maxExtracted)) {
              setNextStartingSlot(slot);
              return true;
            }
          }
        }
      }
      return false;
    }

    private boolean canExtractItem(ItemStack itemStack) {
      if(itemStack == null) {
        return false;
      }
      ItemFilter filter = con.getInputFilter(conDir);
      if(filter == null) {
        return true;
      }
      return filter.doesItemPassFilter(itemStack);
    }

    private boolean doTransfer(ItemStack extractedItem, int slot, int maxExtract) {
      if(extractedItem == null) {
        return false;
      }
      ItemStack toExtract = extractedItem.copy();
      toExtract.stackSize = Math.min(maxExtract, toExtract.stackSize);
      int numInserted = insertIntoTargets(toExtract);
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
      tickDeficit = Math.round(numInserted * con.getTickTimePerItem());
      return true;

    }

    private int insertIntoTargets(ItemStack toExtract) {
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
      return ItemUtil.doInsertItem(inv, item, ForgeDirection.values()[inventorySide]);
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
