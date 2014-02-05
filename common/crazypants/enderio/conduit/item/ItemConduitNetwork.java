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
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import crazypants.enderio.Config;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConnectionMode;
import crazypants.enderio.conduit.IConduit;
import crazypants.util.BlockCoord;
import crazypants.util.InventoryWrapper;
import crazypants.util.ItemUtil;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  private long timeAtLastApply;

  private final List<NetworkedInventory> inventories = new ArrayList<ItemConduitNetwork.NetworkedInventory>();
  private final Map<BlockCoord, NetworkedInventory> invMap = new HashMap<BlockCoord, ItemConduitNetwork.NetworkedInventory>();

  private final Map<BlockCoord, IItemConduit> conMap = new HashMap<BlockCoord, IItemConduit>();

  private boolean requiresSort = true;

  public ItemConduitNetwork() {
    super(IItemConduit.class);
  }

  @Override
  public Class<IItemConduit> getBaseConduitType() {
    return IItemConduit.class;
  }

  @Override
  public void addConduit(IItemConduit con) {
    super.addConduit(con);
    conMap.put(con.getLocation(), con);

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
    //    long start = System.nanoTime();
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        ni.updateInsertOrder();
      }
      ni.onTick(tick);
    }

    //    if(requiresSort) {
    //      long took = System.nanoTime() - start;
    //      double secs = took / 1000000000.0;
    //      System.out.println("Sortinging item network: took " + took + " nano " + secs + " secs, " + (secs * 1000) + " millis");
    //    }

    requiresSort = false;

  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  private static int MAX_SLOT_CHECK_PER_TICK = 64;

  class NetworkedInventory {

    ISidedInventory inv;
    IItemConduit con;
    ForgeDirection conDir;
    BlockCoord location;
    int inventorySide;

    List<Target> sendPriority = new ArrayList<Target>();

    private int extractFromSlot = -1;

    int tickDeficit;

    //work around for a vanilla chest changing into a double chest without doing unnedded checks all the time 
    boolean recheckInv = false;
    private int delay = 0;
    IInventory origInv;

    NetworkedInventory(IInventory inv, IItemConduit con, ForgeDirection conDir, BlockCoord location) {

      inventorySide = conDir.getOpposite().ordinal();

      this.con = con;
      this.conDir = conDir;
      this.location = location;

      if(inv instanceof ISidedInventory) {
        this.inv = (ISidedInventory) inv;
      } else {
        if(inv instanceof TileEntityChest) {
          recheckInv = true;
        }
        this.inv = new InventoryWrapper(inv);
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

    private void setNextStartingSlot(int slot) {
      extractFromSlot = slot;
      extractFromSlot--;
    }

    private boolean transferItems() {

      if(recheckInv) {
        //Re-check vanilla chests twice a second to make sure they haven't become double chests 
        delay++;
        if(delay % 10 == 0) {
          inv = new InventoryWrapper(((InventoryWrapper) inv).getWrappedInv());
          delay = 0;
        }
      }

      int[] slotIndices = inv.getAccessibleSlotsFromSide(inventorySide);
      if(slotIndices == null) {
        return false;
      }
      int numSlots = slotIndices.length;
      ItemStack extractItem = null;
      int maxExtracted = con.getMaximumExtracted();

      int slot = -1;
      int slotChecksPerTick = Math.min(numSlots, MAX_SLOT_CHECK_PER_TICK);
      for (int i = 0; i < slotChecksPerTick; i++) {
        int index = nextSlot(numSlots);
        slot = slotIndices[index];
        ItemStack item = inv.getStackInSlot(slot);
        if(canExtractItem(item)) {
          extractItem = item.copy();
          if(inv.canExtractItem(index, extractItem, inventorySide)) {
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
          inv.onInventoryChanged();
        } else {
          inv.setInventorySlotContents(slot, null);
          inv.onInventoryChanged();
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

      Map<BlockCoord, Target> result = new HashMap<BlockCoord, Target>();

      for (NetworkedInventory other : inventories) {
        if((con.isSelfFeedEnabled(conDir) || (other != this))
            && other.canInsert()
            && con.getInputColor(conDir) == other.con.getOutputColor(other.conDir)) {

          if(Config.itemConduitUsePhyscialDistance) {
            sendPriority.add(new Target(other, distanceTo(other), other.isSticky()));
          } else {
            result.put(other.location, new Target(other, 9999999, other.isSticky()));
          }
        }
      }

      if(Config.itemConduitUsePhyscialDistance) {
        Collections.sort(sendPriority);
      } else {
        if(!result.isEmpty()) {
          Map<BlockCoord, Integer> visited = new HashMap<BlockCoord, Integer>();
          List<BlockCoord> steps = new ArrayList<BlockCoord>();
          steps.add(con.getLocation());
          calculateDistances(result, visited, steps, 0);

          sendPriority.addAll(result.values());

          Collections.sort(sendPriority);
        }
      }

    }

    private void calculateDistances(Map<BlockCoord, Target> result, Map<BlockCoord, Integer> visited, List<BlockCoord> steps, int distance) {
      if(steps == null || steps.isEmpty()) {
        return;
      }

      ArrayList<BlockCoord> nextSteps = new ArrayList<BlockCoord>();
      for (BlockCoord bc : steps) {
        IItemConduit con = conMap.get(bc);
        if(con != null) {
          for (ForgeDirection dir : con.getExternalConnections()) {
            Target target = result.get(bc.getLocation(dir));
            if(target != null && target.distance > distance) {
              target.distance = distance;
            }
          }

          if(!visited.containsKey(bc)) {
            visited.put(bc, distance);
          } else {
            int prevDist = visited.get(bc);
            if(prevDist <= distance) {
              continue;
            }
            visited.put(bc, distance);
          }

          for (ForgeDirection dir : con.getConduitConnections()) {
            nextSteps.add(bc.getLocation(dir));
          }
        }
      }
      calculateDistances(result, visited, nextSteps, distance + 1);
    }

    private int distanceTo(NetworkedInventory other) {
      return con.getLocation().distanceSquared(other.con.getLocation());
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

}
