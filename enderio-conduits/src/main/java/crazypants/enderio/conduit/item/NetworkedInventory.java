package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.enderio.core.common.util.RoundRobinIterator;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.capability.ItemTools;
import crazypants.enderio.base.conduit.ConnectionMode;
import crazypants.enderio.base.conduit.IConduit;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.ILimitedItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.conduit.config.ConduitConfig;
import crazypants.enderio.util.Prep;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class NetworkedInventory implements INetworkedInventory {

  private static final boolean SIMULATE = true;
  private static final boolean EXECUTE = false;

  private final @Nonnull IItemConduit con;
  private final @Nonnull EnumFacing conDir;
  private final @Nonnull BlockPos location;
  private final @Nonnull EnumFacing inventorySide;

  private final @Nonnull List<Target> sendPriority = new ArrayList<Target>();
  private final @Nonnull RoundRobinIterator<Target> rrIter = new RoundRobinIterator<Target>(sendPriority);

  private int extractFromSlot = -1;

  private int tickDeficit;

  // TODO Inventory
  // boolean inventoryPanel = false;

  private final @Nonnull World world;
  private final @Nonnull ItemConduitNetwork network;
  private final @Nonnull String invName;

  NetworkedInventory(@Nonnull ItemConduitNetwork network, @Nonnull IItemConduit con, @Nonnull EnumFacing conDir, @Nonnull IItemHandler inv,
      @Nonnull BlockPos location) {
    this.network = network;
    inventorySide = conDir.getOpposite();
    this.con = con;
    this.conDir = conDir;
    this.location = location;
    world = con.getBundle().getBundleworld();

    IBlockState bs = world.getBlockState(location);
    invName = bs.getBlock().getLocalizedName();

    // TileEntity te = world.getTileEntity(location);
    // if(te instanceof TileInventoryPanel) {
    // inventoryPanel = true;
    // }
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return location;
  }

  @Override
  public @Nonnull IItemConduit getCon() {
    return con;
  }

  @Override
  public @Nonnull EnumFacing getConDir() {
    return conDir;
  }

  @Override
  public @Nonnull List<Target> getSendPriority() {
    return sendPriority;
  }

  @Override
  public boolean hasTarget(@Nonnull IConduit conduit, @Nonnull EnumFacing dir) {
    if (conduit instanceof IItemConduit) {
      for (Target t : sendPriority) {
        if (t.inv.getCon() == conduit && t.inv.getConDir() == dir) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean canExtract() {
    ConnectionMode mode = con.getConnectionMode(conDir);
    return mode == ConnectionMode.INPUT || mode == ConnectionMode.IN_OUT;
  }

  @Override
  public boolean canInsert() {
    // if(inventoryPanel) {
    // return false;
    // }
    ConnectionMode mode = con.getConnectionMode(conDir);
    return mode == ConnectionMode.OUTPUT || mode == ConnectionMode.IN_OUT;
  }

  // boolean isInventoryPanel() {
  // return inventoryPanel;
  // }

  @Override
  public boolean isSticky() {
    return con.getOutputFilter(conDir) != null && con.getOutputFilter(conDir).isValid() && con.getOutputFilter(conDir).isSticky();
  }

  @Override
  public int getPriority() {
    return con.getOutputPriority(conDir);
  }

  @Override
  public void onTick() {
    if (tickDeficit > 0 || !canExtract() || !con.isExtractionRedstoneConditionMet(conDir)) {
      // do nothing
    } else {
      transferItems();
    }

    tickDeficit--;
    if (tickDeficit < -1) {
      // Sleep for a second before checking again.
      tickDeficit = 20;
    }
  }

  private int nextSlot(int numSlots) {
    ++extractFromSlot;
    if (extractFromSlot >= numSlots || extractFromSlot < 0) {
      extractFromSlot = 0;
    }
    return extractFromSlot;
  }

  private void setNextStartingSlot(int slot) {
    extractFromSlot = slot;
    extractFromSlot--;
  }

  private boolean transferItems() {
    final IItemHandler inventory = getInventory();
    if (inventory == null) {
      return false;
    }
    final int numSlots = inventory.getSlots();
    if (numSlots < 1) {
      return false;
    }

    final int maxExtracted = con.getMaximumExtracted(conDir);
    final IItemFilter filter = con.getInputFilter(conDir);

    int slot = -1;
    final int slotChecksPerTick = Math.min(numSlots, ItemConduitNetwork.MAX_SLOT_CHECK_PER_TICK);
    for (int i = 0; i < slotChecksPerTick; i++) {
      slot = nextSlot(numSlots);
      ItemStack item = inventory.extractItem(slot, maxExtracted, SIMULATE);
      if (Prep.isValid(item)) {

        if (filter instanceof ILimitedItemFilter && filter.isLimited()) {
          final int count = filter.getMaxCountThatPassesFilter(this, item);
          if (count <= 0) { // doesn't pass filter
            item = Prep.getEmpty();
          } else if (count < Integer.MAX_VALUE) { // some limit
            final ItemStack stackInSlot = inventory.getStackInSlot(slot);
            if (stackInSlot.getCount() <= count) { // there's less than the limit in there
              item = Prep.getEmpty();
            } else if (stackInSlot.getCount() - item.getCount() < count) { // we are trying to extract more than allowed
              item = inventory.extractItem(slot, stackInSlot.getCount() - count, SIMULATE);
            }
          }
        } else if (filter != null && !filter.doesItemPassFilter(this, item)) {
          item = Prep.getEmpty();
        }

        if (Prep.isValid(item) && doTransfer(inventory, item, slot)) {
          setNextStartingSlot(slot);
          return true;
        }
      }
    }
    return false;
  }

  private boolean doTransfer(@Nonnull IItemHandler inventory, @Nonnull ItemStack extractedItem, int slot) {
    int numInserted = insertIntoTargets(extractedItem.copy());
    if (numInserted <= 0) {
      return false;
    }
    ItemStack extracted = inventory.extractItem(slot, numInserted, EXECUTE);
    if (Prep.isInvalid(extracted) || extracted.getCount() != numInserted || extracted.getItem() != extractedItem.getItem()) {
      Log.warn("NetworkedInventory.itemExtracted: Inserted " + numInserted + " " + extractedItem.getDisplayName() + " but only removed "
          + (Prep.isInvalid(extracted) ? "null" : extracted.getCount() + " " + extracted.getDisplayName()) + " from " + inventory + " at " + location);
    }
    onItemExtracted(slot, numInserted);
    return true;
  }

  public void onItemExtracted(int slot, int numInserted) {
    con.itemsExtracted(numInserted, slot);
    tickDeficit = Math.round(numInserted * con.getTickTimePerItem(conDir));
  }

  @Override
  public int insertIntoTargets(@Nonnull ItemStack toExtract) {
    if (Prep.isInvalid(toExtract)) {
      return 0;
    }

    int totalToInsert = toExtract.getCount();
    int leftToInsert = totalToInsert;
    boolean matchedStickyInput = false;

    Iterable<Target> targets = getTargetIterator();

    // for (Target target : sendPriority) {
    for (Target target : targets) {
      if (target.stickyInput && !matchedStickyInput) {
        IItemFilter of = ((IItemConduit) target.inv.getCon()).getOutputFilter(target.inv.getConDir());
        matchedStickyInput = of != null && of.isValid() && of.doesItemPassFilter(this, toExtract);
      }
      if (target.stickyInput || !matchedStickyInput) {
        int inserted = target.inv.insertItem(toExtract);
        if (inserted > 0) {
          toExtract.shrink(inserted);
          leftToInsert -= inserted;
        }
        if (leftToInsert <= 0) {
          return totalToInsert;
        }
      }
    }
    return totalToInsert - leftToInsert;
  }

  private Iterable<Target> getTargetIterator() {
    if (con.isRoundRobinEnabled(conDir)) {
      return rrIter;
    }
    return sendPriority;
  }

  @Override
  public int insertItem(@Nonnull ItemStack item) {
    if (!canInsert() || Prep.isInvalid(item)) {
      return 0;
    }
    IItemFilter filter = con.getOutputFilter(conDir);
    if (filter instanceof ILimitedItemFilter && filter.isLimited()) {
      final int count = filter.getMaxCountThatPassesFilter(this, item);
      if (count <= 0) {
        return 0;
      } else {
        final int maxInsert = ItemTools.getInsertLimit(getInventory(), item, count);
        if (maxInsert <= 0) {
          return 0;
        } else if (maxInsert < item.getCount()) {
          item = item.copy();
          item.setCount(maxInsert);
        }
      }
    } else if (filter != null && !filter.doesItemPassFilter(this, item)) {
      return 0;
    }
    return ItemTools.doInsertItem(getInventory(), item);
  }

  @Override
  public void updateInsertOrder() {
    sendPriority.clear();
    if (!canExtract()) {
      return;
    }
    List<Target> result = new ArrayList<NetworkedInventory.Target>();

    for (INetworkedInventory other : network.inventories) {
      if ((con.isSelfFeedEnabled(conDir) || (other != this)) && other.canInsert()
          && con.getInputColor(conDir) == ((IItemConduit) other.getCon()).getOutputColor(other.getConDir())) {

        if (ConduitConfig.usePhyscialDistance.get()) {
          sendPriority.add(new Target(other, distanceTo(other), other.isSticky(), other.getPriority()));
        } else {
          result.add(new Target(other, 9999999, other.isSticky(), other.getPriority()));
        }
      }
    }

    if (ConduitConfig.usePhyscialDistance.get()) {
      Collections.sort(sendPriority);
    } else {
      if (!result.isEmpty()) {
        Map<BlockPos, Integer> visited = new HashMap<BlockPos, Integer>();
        List<BlockPos> steps = new ArrayList<BlockPos>();
        steps.add(con.getBundle().getLocation());
        calculateDistances(result, visited, steps, 0);

        sendPriority.addAll(result);

        Collections.sort(sendPriority);
      }
    }

  }

  private void calculateDistances(@Nonnull List<Target> targets, @Nonnull Map<BlockPos, Integer> visited, @Nonnull List<BlockPos> steps, int distance) {
    if (steps.isEmpty()) {
      return;
    }

    ArrayList<BlockPos> nextSteps = new ArrayList<BlockPos>();
    for (BlockPos pos : steps) {
      IItemConduit con1 = network.conMap.get(pos);
      if (con1 != null) {
        for (EnumFacing dir : con1.getExternalConnections()) {
          Target target = getTarget(targets, con1, dir);
          if (target != null && target.distance > distance) {
            target.distance = distance;
          }
        }

        if (!visited.containsKey(pos)) {
          visited.put(pos, distance);
        } else {
          int prevDist = visited.get(pos);
          if (prevDist <= distance) {
            continue;
          }
          visited.put(pos, distance);
        }

        for (EnumFacing dir : con1.getConduitConnections()) {
          nextSteps.add(pos.offset(dir));
        }
      }
    }
    calculateDistances(targets, visited, nextSteps, distance + 1);
  }

  private Target getTarget(@Nonnull List<Target> targets, @Nonnull IItemConduit con1, @Nonnull EnumFacing dir) {
    for (Target target : targets) {
      if (target != null && target.inv != null) {
        if (target.inv.getConDir() == dir && target.inv.getCon().getBundle().getLocation().equals(con1.getBundle().getLocation())) {
          return target;
        }
      }
    }
    return null;
  }

  private int distanceTo(INetworkedInventory other) {
    // TODO Check if this should be a double or int
    return (int) con.getBundle().getLocation().distanceSq(other.getCon().getBundle().getLocation());
  }

  @Override
  public @Nullable IItemHandler getInventory() {
    return ItemTools.getExternalInventory(world, location, inventorySide);
  }

  public EnumFacing getInventorySide() {
    return inventorySide;
  }

  /**
   * Class for storing the Target Inventory
   */
  static class Target implements Comparable<Target> {
    INetworkedInventory inv;
    int distance;
    boolean stickyInput;
    int priority;

    Target(@Nonnull INetworkedInventory inv, int distance, boolean stickyInput, int priority) {
      this.inv = inv;
      this.distance = distance;
      this.stickyInput = stickyInput;
      this.priority = priority;
    }

    @Override
    public int compareTo(Target o) {
      if (stickyInput && !o.stickyInput) {
        return -1;
      }
      if (!stickyInput && o.stickyInput) {
        return 1;
      }
      if (priority != o.priority) {
        return ItemConduitNetwork.compare(o.priority, priority);
      }
      return ItemConduitNetwork.compare(distance, o.distance);
    }

  }

  @Override
  public @Nonnull String getLocalizedInventoryName() {
    return invName;
  }

  public boolean isAt(BlockPos pos) {
    return location.equals(pos);
  }

}
