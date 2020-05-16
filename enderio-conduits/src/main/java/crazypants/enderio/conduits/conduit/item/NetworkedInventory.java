package crazypants.enderio.conduits.conduit.item;

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
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.conduits.config.ConduitConfig;
import crazypants.enderio.util.Prep;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

public class NetworkedInventory {

  private static final boolean SIMULATE = true;
  private static final boolean EXECUTE = false;

  private final @Nonnull IItemConduit con;
  private final @Nonnull EnumFacing conDir;
  private final @Nonnull BlockPos location;
  private final @Nonnull EnumFacing inventorySide;

  private final @Nonnull List<Target> sendPriority = new ArrayList<Target>();
  private final @Nonnull RoundRobinIterator<Target> rrIter = new RoundRobinIterator<Target>(sendPriority);

  private int extractFromSlot = -1;

  private int tickDeficit = 100; // wait 5 seconds before doing anything after being created

  private final @Nonnull World world;
  private final @Nonnull ItemConduitNetwork network;

  NetworkedInventory(@Nonnull ItemConduitNetwork network, @Nonnull IItemConduit con, @Nonnull EnumFacing conDir, @Nonnull IItemHandler inv,
      @Nonnull BlockPos location) {
    this.network = network;
    this.inventorySide = conDir.getOpposite();
    this.con = con;
    this.conDir = conDir;
    this.location = location;
    this.world = con.getBundle().getBundleworld();
  }

  public @Nonnull BlockPos getLocation() {
    return location;
  }

  public @Nonnull IItemConduit getCon() {
    return con;
  }

  public @Nonnull EnumFacing getConDir() {
    return conDir;
  }

  public @Nonnull List<Target> getSendPriority() {
    return sendPriority;
  }

  public boolean hasTarget(@Nonnull IItemConduit conduit, @Nonnull EnumFacing dir) {
    for (Target t : sendPriority) {
      if (t.inv.getCon() == conduit && t.inv.getConDir() == dir) {
        return true;
      }
    }
    return false;
  }

  private boolean canExtract() {
    return con.getConnectionMode(conDir).acceptsInput();
  }

  private boolean canInsert() {
    return con.getConnectionMode(conDir).acceptsOutput();
  }

  private boolean isSticky() {
    final IItemFilter outputFilter = valid(con.getOutputFilter(conDir));
    return outputFilter != null && outputFilter.isSticky();
  }

  private int getPriority() {
    return con.getOutputPriority(conDir);
  }

  public boolean shouldTick() {
    if (tickDeficit > 0) {
      tickDeficit--;
      return false;
    }
    if (!canExtract() || !con.isExtractionRedstoneConditionMet(conDir)) {
      // Cannot extract, sleep for a second before checking again.
      tickDeficit = ConduitConfig.sleepBetweenTries.get();
      return false;
    }
    return true;
  }

  public void onTick() {
    if (!transferItems()) {
      // Transfer failed, sleep for 2.5 seconds before checking again.
      tickDeficit = ConduitConfig.sleepBetweenFailedTries.get();
      return;
    }
    if (tickDeficit <= 0) {
      // Transfer successful but the transferItems() didn't set a new tickDeficit, sleep for a second before checking again.
      tickDeficit = ConduitConfig.sleepBetweenTries.get();
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
    final IItemFilter filter = valid(con.getInputFilter(conDir));

    int slotChecksPerTick = ConduitConfig.maxSlotCheckPerTick.get();
    for (int i = 0; i < numSlots && i < slotChecksPerTick; i++) {
      final int slot = nextSlot(numSlots);
      ItemStack item = inventory.extractItem(slot, maxExtracted, SIMULATE);
      if (Prep.isValid(item)) {
        if (filter != null) {
          if (filter.isLimited()) {
            final int count = filter.getMaxCountThatPassesFilter(inventory, item);
            if (count <= 0) { // doesn't pass filter
              continue; // skip slot
            } else if (count < Integer.MAX_VALUE) { // some limit
              final ItemStack stackInSlot = inventory.getStackInSlot(slot);
              if (stackInSlot.getCount() <= count) { // there's less than the limit in there
                continue; // skip slot
              } else if (stackInSlot.getCount() - item.getCount() < count) { // we are trying to extract more than allowed
                item = inventory.extractItem(slot, stackInSlot.getCount() - count, SIMULATE);
                if (Prep.isInvalid(item)) {
                  continue; // skip slot
                }
              }
            }
          } else if (!filter.doesItemPassFilter(inventory, item)) {
            continue; // skip slot
          }
        }

        if (doTransfer(inventory, item, slot)) {
          if (inventory.getStackInSlot(slot).isEmpty()) {
            setNextStartingSlot(slot + 1);
          } else {
            setNextStartingSlot(slot);
          }
          return true;
        }
      } else {
        // Checking empty slots is cheap, so don't count them towards the limit
        slotChecksPerTick++;
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
      if (extracted.getCount() < numInserted && (extracted.getCount() == 0 || extracted.getItem() == extractedItem.getItem())) {
        Log.warn("NetworkedInventory.itemExtracted: Inserted " + numInserted + " " + extractedItem.getDisplayName() + " but only removed "
            + extracted.getCount() + " " + extracted.getDisplayName() + " from " + inventory + " at " + location + ". This means that "
            + (numInserted - extracted.getCount()) + " items were just duped by " + inventory + "!");
      } else {
      Log.warn("NetworkedInventory.itemExtracted: Inserted " + numInserted + " " + extractedItem.getDisplayName() + " but only removed "
            + extracted.getCount() + " " + extracted.getDisplayName() + " from " + inventory + " at " + location);
      }
    }
    onItemExtracted(slot, numInserted);
    return true;
  }

  private void onItemExtracted(int slot, int numInserted) {
    con.itemsExtracted(numInserted, slot);
    tickDeficit = Math.round(numInserted * con.getTickTimePerItem(conDir));
  }

  private int insertIntoTargets(@Nonnull ItemStack toInsert) {
    if (Prep.isInvalid(toInsert)) {
      return 0;
    }

    final int totalToInsert = toInsert.getCount();
    // when true, a sticky filter has claimed this item and so only sticky outputs are allowed to handle it. sticky outputs are first in the target
    // list, so all sticky outputs are queried before any non-sticky one.
    boolean matchedStickyOutput = false;

    for (Target target : getTargetIterator()) {
      final IItemFilter filter = valid(target.inv.getCon().getOutputFilter(target.inv.getConDir()));
      if (target.stickyInput && !matchedStickyOutput && filter != null) {
        matchedStickyOutput = filter.doesItemPassFilter(target.inv.getInventory(), toInsert);
      }
      if (target.stickyInput || !matchedStickyOutput) {
        toInsert.shrink(positive(target.inv.insertItem(toInsert, filter)));
        if (Prep.isInvalid(toInsert)) {
          // everything has been inserted. we're done.
          break;
        }
      } else if (!target.stickyInput && matchedStickyOutput) {
        // item has been claimed by a sticky output but there are no sticky outputs left in targets, so we can stop checking
        break;
      }
    }

    return totalToInsert - toInsert.getCount();
  }

  private static final IItemFilter valid(IItemFilter filter) {
    return filter != null && filter.isValid() ? filter : null;
  }

  private static final int positive(int x) {
    return x > 0 ? x : 0;
  }

  private Iterable<Target> getTargetIterator() {
    if (con.isRoundRobinEnabled(conDir)) {
      return rrIter;
    }
    return sendPriority;
  }

  private int insertItem(@Nonnull ItemStack item, IItemFilter filter) {
    if (!canInsert() || Prep.isInvalid(item)) {
      return 0;
    }
    final IItemHandler inventory = getInventory();
    if (inventory == null) {
      return 0;
    }
    if (filter != null) {
      if (filter.isLimited()) {
        final int count = filter.getMaxCountThatPassesFilter(inventory, item);
        if (count <= 0) {
          return 0;
        } else {
          final int maxInsert = ItemTools.getInsertLimit(inventory, item, count);
          if (maxInsert <= 0) {
            return 0;
          } else if (maxInsert < item.getCount()) {
            item = item.copy();
            item.setCount(maxInsert);
          }
        }
      } else if (!filter.doesItemPassFilter(inventory, item)) {
        return 0;
      }
    }
    return ItemTools.doInsertItem(inventory, item);
  }

  public void updateInsertOrder() {
    sendPriority.clear();
    if (!canExtract()) {
      return;
    }

    List<Target> result = new ArrayList<NetworkedInventory.Target>();
    for (NetworkedInventory other : network.getInventories()) {
      if (!other.canInsert())
        continue;
      if (con.getInputColor(conDir) != other.getCon().getOutputColor(other.getConDir()))
        continue;

      if (other == this) {
        if (!con.isSelfFeedEnabled(conDir))
          continue;
      } else {
        // If the source have enabled input and have higher or equal priority
        // than the potential target, don't transfer there
        if (this.canInsert() && Integer.compare(this.getPriority(), other.getPriority()) >= 0) {
          continue;
        }
      }

      if (ConduitConfig.usePhyscialDistance.get()) {
        sendPriority.add(new Target(other, distanceTo(other), other.isSticky(), other.getPriority()));
      } else {
        result.add(new Target(other, 9999999, other.isSticky(), other.getPriority()));
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
      IItemConduit con1 = network.getConMap().get(pos);
      if (con1 != null) {
        for (EnumFacing dir : con1.getExternalConnections()) {
          if (dir != null) {
            Target target = getTarget(targets, con1, dir);
            if (target != null && target.distance > distance) {
              target.distance = distance;
            }
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
          if (dir != null) {
            nextSteps.add(pos.offset(dir));
          }
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

  private int distanceTo(NetworkedInventory other) {
    // TODO Check if this should be a double or int
    return (int) con.getBundle().getLocation().distanceSq(other.getCon().getBundle().getLocation());
  }

  public @Nullable IItemHandler getInventory() {
    return ItemTools.getExternalInventory(world, location, inventorySide);
  }

  /**
   * Class for storing the Target Inventory
   */
  static class Target implements Comparable<Target> {
    final NetworkedInventory inv;
    int distance;
    final boolean stickyInput;
    final int priority;

    Target(@Nonnull NetworkedInventory inv, int distance, boolean stickyInput, int priority) {
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
        return Integer.compare(o.priority, priority);
      }
      return Integer.compare(distance, o.distance);
    }

  }

  public @Nonnull String getLocalizedInventoryName() {
    // only used by the conduit probe, no need to cache this
    return world.getBlockState(location).getBlock().getLocalizedName();
  }

}
