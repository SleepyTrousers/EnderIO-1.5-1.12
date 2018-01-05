package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.base.machine.invpanel.server.InventoryDatabaseServer;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory.Target;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.IItemHandler;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  final List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
  private final Map<BlockCoord, List<NetworkedInventory>> invMap = new HashMap<BlockCoord, List<NetworkedInventory>>();

  final Map<BlockCoord, IItemConduit> conMap = new HashMap<BlockCoord, IItemConduit>();

  private boolean requiresSort = true;

  private boolean doingSend = false;

  private int changeCount;

  private InventoryDatabaseServer database;

  public ItemConduitNetwork() {
    super(IItemConduit.class, IItemConduit.class);
  }

  @Override
  public void addConduit(IItemConduit con) {
    super.addConduit(con);
    conMap.put(con.getLocation(), con);

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

  public void inventoryAdded(IItemConduit itemConduit, EnumFacing direction, BlockPos pos, IItemHandler externalInventory) {
    BlockCoord bc = new BlockCoord(pos);
    NetworkedInventory inv = new NetworkedInventory(this, itemConduit, direction, externalInventory, bc);
    inventories.add(inv);
    getOrCreate(bc).add(inv);
    requiresSort = true;
  }
  
  public NetworkedInventory getInventory(IItemConduit conduit, EnumFacing dir) {
    for(NetworkedInventory inv : inventories) {
      if(inv.con == conduit && inv.conDir == dir) {
        return inv;
      }
    }
    return null;
  }

  public List<NetworkedInventory> getInventoryPanelSources() {
    ArrayList<NetworkedInventory> res = new ArrayList<NetworkedInventory>();
    for(NetworkedInventory inv : inventories) {
      if(inv.con.hasInventoryPanelUpgrade(inv.conDir)) {
        res.add(inv);
      }
    }
    return res;
  }

  private List<NetworkedInventory> getOrCreate(BlockCoord bc) {
    List<NetworkedInventory> res = invMap.get(bc);
    if(res == null) {
      res = new ArrayList<NetworkedInventory>();
      invMap.put(bc, res);
    }
    return res;
  }

  public void inventoryRemoved(ItemConduit itemConduit, BlockPos pos) {
    BlockCoord bc = new BlockCoord(pos);
    List<NetworkedInventory> invs = getOrCreate(bc);
    NetworkedInventory remove = null;
    for (NetworkedInventory ni : invs) {
      if(ni.con.getLocation().equals(itemConduit.getLocation())) {
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

  public void inventoryPanelSourcesChanged() {
    changeCount++;
  }

  public int getChangeCount() {
    return changeCount;
  }

  public boolean hasDatabase() {
    return database != null;
  }

  public InventoryDatabaseServer getDatabase() {
    check: {
      if(database == null) {
        database = new InventoryDatabaseServer(this);
      } else if(database.isCurrent()) {
        break check;
      }
      database.updateNetworkSources();
    }
    return database;
  }

  @Override
  public void destroyNetwork() {
    super.destroyNetwork();
    if(database != null) {
      database.resetDatabase();
      database = null;
    }
  }

  // not used?
  @Deprecated
  public ItemStack sendItems(ItemConduit itemConduit, ItemStack item, EnumFacing side) {
    if(doingSend) {
      return item;
    }

    if(item == null) {
      return item;
    }

    try {
      doingSend = true;
      BlockCoord loc = itemConduit.getLocation().getLocation(side);

      ItemStack result = item.copy();
      List<NetworkedInventory> invs = getOrCreate(loc);
      for (NetworkedInventory inv : invs) {

        if(inv.con.getLocation().equals(itemConduit.getLocation())) {
          int numInserted = inv.insertIntoTargets(item.copy());
          if(numInserted >= item.stackSize) {
            return null;
          }
          result.stackSize -= numInserted;
        }
      }
      return result;
    } finally {
      doingSend = false;
    }
  }

  public List<String> getTargetsForExtraction(BlockCoord extractFrom, IItemConduit con, ItemStack input) {
    List<String> result = new ArrayList<String>();

    List<NetworkedInventory> invs = getOrCreate(extractFrom);
    for (NetworkedInventory source : invs) {

      if(source.con.getLocation().equals(con.getLocation())) {
        if(source.sendPriority != null) {
          for (Target t : source.sendPriority) {
            IItemFilter f = t.inv.con.getOutputFilter(t.inv.conDir);
            if(input == null || f == null || f.doesItemPassFilter(t.inv, input)) {
              String s = t.inv.getLocalizedInventoryName() + " " + new BlockCoord(t.inv.location).chatString() + " Distance [" + t.distance + "] ";
              result.add(s);
            }
          }
        }
      }
    }

    return result;
  }

  public List<String> getInputSourcesFor(IItemConduit con, EnumFacing dir, ItemStack input) {
    List<String> result = new ArrayList<String>();
    for (NetworkedInventory inv : inventories) {
      if(inv.hasTarget(con, dir)) {
        IItemFilter f = inv.con.getInputFilter(inv.conDir);
        if(input == null || f == null || f.doesItemPassFilter(inv, input)) {
          result.add(inv.getLocalizedInventoryName() + " " + new BlockCoord(inv.location).chatString());
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
    if(database != null) {
      theProfiler.startSection("DatabaseTick");
      database.tick();
      theProfiler.endSection();
    }
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  static int MAX_SLOT_CHECK_PER_TICK = 64;

}
