package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;

import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.filter.IItemFilter;
import crazypants.enderio.base.filter.INetworkedInventory;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory.Target;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.items.IItemHandler;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  final List<INetworkedInventory> inventories = new ArrayList<INetworkedInventory>();
  private final Map<BlockPos, List<INetworkedInventory>> invMap = new HashMap<BlockPos, List<INetworkedInventory>>();

  final Map<BlockPos, IItemConduit> conMap = new HashMap<BlockPos, IItemConduit>();

  private boolean requiresSort = true;

  private boolean doingSend = false;

  private int changeCount;
  // TODO Inventory
  // private InventoryDatabaseServer database;

  public ItemConduitNetwork() {
    super(IItemConduit.class, IItemConduit.class);
  }

  @Override
  public void addConduit(@Nonnull IItemConduit con) {
    super.addConduit(con);
    conMap.put(con.getBundle().getLocation(), con);

    TileEntity te = con.getBundle().getEntity();
    if (te != null) {
      for (EnumFacing direction : con.getExternalConnections()) {
        IItemHandler extCon = con.getExternalInventory(direction);
        if (extCon != null) {
          BlockPos p = te.getPos().offset(direction);
          inventoryAdded(con, direction, p, extCon);
        }
      }
    }
  }

  public void inventoryAdded(@Nonnull IItemConduit itemConduit, @Nonnull EnumFacing direction, @Nonnull BlockPos pos, @Nonnull IItemHandler externalInventory) {
    INetworkedInventory inv = new NetworkedInventory(this, itemConduit, direction, externalInventory, pos);
    inventories.add(inv);
    getOrCreate(pos).add(inv);
    requiresSort = true;
  }

  public INetworkedInventory getInventory(@Nonnull IItemConduit conduit, @Nonnull EnumFacing dir) {
    for (INetworkedInventory inv : inventories) {
      if (inv.getCon() == conduit && inv.getConDir() == dir) {
        return inv;
      }
    }
    return null;
  }

  // public List<NetworkedInventory> getInventoryPanelSources() {
  // ArrayList<NetworkedInventory> res = new ArrayList<NetworkedInventory>();
  // for(NetworkedInventory inv : inventories) {
  // if(inv.con.hasInventoryPanelUpgrade(inv.conDir)) {
  // res.add(inv);
  // }
  // }
  // return res;
  // }

  private List<INetworkedInventory> getOrCreate(@Nonnull BlockPos pos) {
    List<INetworkedInventory> res = invMap.get(pos);
    if (res == null) {
      res = new ArrayList<INetworkedInventory>();
      invMap.put(pos, res);
    }
    return res;
  }

  public void inventoryRemoved(@Nonnull ItemConduit itemConduit, @Nonnull BlockPos pos) {
    List<INetworkedInventory> invs = getOrCreate(pos);
    INetworkedInventory remove = null;
    for (INetworkedInventory ni : invs) {
      if (ni.getCon().getBundle().getLocation().equals(itemConduit.getBundle().getLocation())) {
        remove = ni;
        break;
      }
    }
    if (remove != null) {
      invs.remove(remove);
      inventories.remove(remove);
      requiresSort = true;
    }

  }

  public void routesChanged() {
    requiresSort = true;
  }

  // public void inventoryPanelSourcesChanged() {
  // changeCount++;
  // }

  public int getChangeCount() {
    return changeCount;
  }

  // public boolean hasDatabase() {
  // return database != null;
  // }

  // public InventoryDatabaseServer getDatabase() {
  // check: {
  // if(database == null) {
  // database = new InventoryDatabaseServer(this);
  // } else if(database.isCurrent()) {
  // break check;
  // }
  // database.updateNetworkSources();
  // }
  // return database;
  // }

  // @Override
  // public void destroyNetwork() {
  // super.destroyNetwork();
  // if(database != null) {
  // database.resetDatabase();
  // database = null;
  // }
  // }

  // TODO not used?
  @Deprecated
  @Nonnull
  public ItemStack sendItems(ItemConduit itemConduit, @Nonnull ItemStack item, @Nonnull EnumFacing side) {
    if (doingSend) {
      return item;
    }

    if (item.isEmpty()) {
      return item;
    }

    try {
      doingSend = true;
      BlockPos loc = itemConduit.getBundle().getLocation().offset(side);

      ItemStack result = item.copy();
      List<INetworkedInventory> invs = getOrCreate(loc);
      for (INetworkedInventory inv : invs) {

        if (inv.getCon().getBundle().getLocation().equals(itemConduit.getBundle().getLocation())) {
          int numInserted = inv.insertIntoTargets(item.copy());
          if (numInserted >= item.getCount()) {
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

  private IItemHandler getTargetInventory(Target target) {
    if (target.inv != null) {
      return target.inv.getInventory();
    }
    return null;
  }

  public List<String> getTargetsForExtraction(@Nonnull BlockPos extractFrom, @Nonnull IItemConduit con, @Nonnull ItemStack input) {
    List<String> result = new ArrayList<String>();

    List<INetworkedInventory> invs = getOrCreate(extractFrom);
    for (INetworkedInventory source : invs) {

      if (source.getCon().getBundle().getLocation().equals(con.getBundle().getLocation())) {
        List<Target> sendPriority = (List<Target>) source.getSendPriority();
        if (sendPriority != null) {
          for (Target t : sendPriority) {
            IItemFilter f = ((IItemConduit) t.inv.getCon()).getOutputFilter(t.inv.getConDir());
            if (input.isEmpty() || f == null || f.doesItemPassFilter(getTargetInventory(t), input)) {
              String s = t.inv.getLocalizedInventoryName() + " " + t.inv.getLocation().toString() + " Distance [" + t.distance + "] ";
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
    for (INetworkedInventory inv : inventories) {
      if (inv.hasTarget(con, dir)) {
        IItemFilter f = ((IItemConduit) inv.getCon()).getInputFilter(inv.getConDir());
        if (input.isEmpty() || f == null || f.doesItemPassFilter(inv.getInventory(), input)) {
          result.add(inv.getLocalizedInventoryName() + " " + inv.getLocation().toString());
        }
      }
    }
    return result;
  }

  @Override
  public void tickEnd(ServerTickEvent event, Profiler profiler) {
    for (INetworkedInventory ni : inventories) {
      if (requiresSort) {
        Prof.start(profiler, "updateInsertOrder_", ni.getInventory());
        ni.updateInsertOrder();
        Prof.stop(profiler);
      }
      Prof.start(profiler, "NetworkedInventoryTick_", ni.getInventory());
      ni.onTick();
      Prof.stop(profiler);
    }
    if (requiresSort) {
      requiresSort = false;
      changeCount++;
    }
    // if(database != null) {
    // Prof.start(profiler, "DatabaseTick");
    // database.tick();
    // Prof.stop(profiler);
    // }
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  static int MAX_SLOT_CHECK_PER_TICK = 64;

}
