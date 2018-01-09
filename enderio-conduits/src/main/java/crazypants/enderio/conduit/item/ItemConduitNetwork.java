package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory.Target;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  final List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
  private final Map<BlockPos, List<NetworkedInventory>> invMap = new HashMap<BlockPos, List<NetworkedInventory>>();

  final Map<BlockPos, IItemConduit> conMap = new HashMap<BlockPos, IItemConduit>();

  private boolean requiresSort = true;

  private boolean doingSend = false;

  private int changeCount;
// TODO Inventory
//  private InventoryDatabaseServer database;

  public ItemConduitNetwork() {
    super(IItemConduit.class, IItemConduit.class);
  }

  @Override
  public void addConduit(IItemConduit con) {
    super.addConduit(con);
    conMap.put(con.getBundle().getLocation(), con);

    TileEntity te = con.getBundle().getEntity();
    if(te != null) {
      for (EnumFacing direction : con.getExternalConnections()) {
        IItemHandler extCon = con.getExternalInventory(direction);
        if(extCon != null) {
          BlockPos p = te.getPos().offset(direction);
          inventoryAdded(con, direction, p, extCon);
        }
      }
    }
  }

  public void inventoryAdded(@Nonnull IItemConduit itemConduit, @Nonnull EnumFacing direction, @Nonnull BlockPos pos, @Nonnull IItemHandler externalInventory) {
    NetworkedInventory inv = new NetworkedInventory(this, itemConduit, direction, externalInventory, pos);
    inventories.add(inv);
    getOrCreate(pos).add(inv);
    requiresSort = true;
  }
  
  public NetworkedInventory getInventory(@Nonnull IItemConduit conduit, @Nonnull EnumFacing dir) {
    for(NetworkedInventory inv : inventories) {
      if(inv.con == conduit && inv.conDir == dir) {
        return inv;
      }
    }
    return null;
  }

//  public List<NetworkedInventory> getInventoryPanelSources() {
//    ArrayList<NetworkedInventory> res = new ArrayList<NetworkedInventory>();
//    for(NetworkedInventory inv : inventories) {
//      if(inv.con.hasInventoryPanelUpgrade(inv.conDir)) {
//        res.add(inv);
//      }
//    }
//    return res;
//  }

  private List<NetworkedInventory> getOrCreate(@Nonnull BlockPos pos) {
    List<NetworkedInventory> res = invMap.get(pos);
    if(res == null) {
      res = new ArrayList<NetworkedInventory>();
      invMap.put(pos, res);
    }
    return res;
  }

  public void inventoryRemoved(@Nonnull ItemConduit itemConduit, @Nonnull BlockPos pos) {
    List<NetworkedInventory> invs = getOrCreate(pos);
    NetworkedInventory remove = null;
    for (NetworkedInventory ni : invs) {
      if(ni.con.getBundle().getLocation().equals(itemConduit.getBundle().getLocation())) {
        remove = ni;
        break;
      }
    }
    if(remove != null) {
      invs.remove(remove);
      inventories.remove(remove);
      requiresSort = true;
    }

  }

  public void routesChanged() {
    requiresSort = true;
  }

//  public void inventoryPanelSourcesChanged() {
//    changeCount++;
//  }

  public int getChangeCount() {
    return changeCount;
  }

//  public boolean hasDatabase() {
//    return database != null;
//  }

//  public InventoryDatabaseServer getDatabase() {
//    check: {
//      if(database == null) {
//        database = new InventoryDatabaseServer(this);
//      } else if(database.isCurrent()) {
//        break check;
//      }
//      database.updateNetworkSources();
//    }
//    return database;
//  }

//  @Override
//  public void destroyNetwork() {
//    super.destroyNetwork();
//    if(database != null) {
//      database.resetDatabase();
//      database = null;
//    }
//  }

  // TODO not used?
  @Deprecated
  @Nonnull
  public ItemStack sendItems(ItemConduit itemConduit, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
    if(doingSend) {
      return item;
    }

    if(item.isEmpty()) {
      return item;
    }

    try {
      doingSend = true;
      BlockPos loc = itemConduit.getBundle().getLocation().offset(side);

      ItemStack result = item.copy();
      List<NetworkedInventory> invs = getOrCreate(loc);
      for (NetworkedInventory inv : invs) {

        if(inv.con.getBundle().getLocation().equals(itemConduit.getBundle().getLocation())) {
          int numInserted = inv.insertIntoTargets(item.copy());
          if(numInserted >= item.getCount()) {
            return ItemStack.EMPTY;
          }
          result.shrink(numInserted);
        }
      }
      return result;
    } finally {
      doingSend = false;
    }
  }

  public List<String> getTargetsForExtraction(@Nonnull BlockPos extractFrom, @Nonnull IItemConduit con, @Nonnull ItemStack input) {
    List<String> result = new ArrayList<String>();

    List<NetworkedInventory> invs = getOrCreate(extractFrom);
    for (NetworkedInventory source : invs) {

      if(source.con.getBundle().getLocation().equals(con.getBundle().getLocation())) {
        if(source.sendPriority != null) {
          for (Target t : source.sendPriority) {
            IItemFilter f = t.inv.con.getOutputFilter(t.inv.conDir);
            if(input.isEmpty() || f == null || f.doesItemPassFilter(t.inv, input)) {
              String s = t.inv.getLocalizedInventoryName() + " " + t.inv.location.toString() + " Distance [" + t.distance + "] ";
              result.add(s);
            }
          }
        }
      }
    }

    return result;
  }

  public List<String> getInputSourcesFor(@Nonnull IItemConduit con, @Nonnull EnumFacing dir, @Nonnull ItemStack input) {
    List<String> result = new ArrayList<String>();
    for (NetworkedInventory inv : inventories) {
      if(inv.hasTarget(con, dir)) {
        IItemFilter f = inv.con.getInputFilter(inv.conDir);
        if(input.isEmpty()|| f == null || f.doesItemPassFilter(inv, input)) {
          result.add(inv.getLocalizedInventoryName() + " " + inv.location.toString());
        }
      }
    }
    return result;
  }

  @Override
  public void doNetworkTick(Profiler theProfiler) {
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        theProfiler.startSection("updateInsertOrder");
        ni.updateInsertOrder();
        theProfiler.endSection();
      }
      theProfiler.startSection("NetworkedInventoryTick");
      ni.onTick();
      theProfiler.endSection();
    }
    if(requiresSort) {
      requiresSort = false;
      changeCount++;
    }
//    if(database != null) {
//      theProfiler.startSection("DatabaseTick");
//      database.tick();
//      theProfiler.endSection();
//    }
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  static int MAX_SLOT_CHECK_PER_TICK = 64;

}
