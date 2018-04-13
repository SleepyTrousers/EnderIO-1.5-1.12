package crazypants.enderio.conduits.conduit.item;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import crazypants.enderio.base.diagnostics.Prof;
import crazypants.enderio.base.filter.item.IItemFilter;
import crazypants.enderio.conduits.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduits.conduit.item.NetworkedInventory.Target;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.items.IItemHandler;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  final List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
  private final Map<BlockPos, List<NetworkedInventory>> invMap = new HashMap<BlockPos, List<NetworkedInventory>>();

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
    NetworkedInventory inv = new NetworkedInventory(this, itemConduit, direction, externalInventory, pos);
    inventories.add(inv);
    getOrCreate(pos).add(inv);
    requiresSort = true;
  }

  public NetworkedInventory getInventory(@Nonnull IItemConduit conduit, @Nonnull EnumFacing dir) {
    for (NetworkedInventory inv : inventories) {
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

  private List<NetworkedInventory> getOrCreate(@Nonnull BlockPos pos) {
    List<NetworkedInventory> res = invMap.get(pos);
    if (res == null) {
      res = new ArrayList<NetworkedInventory>();
      invMap.put(pos, res);
    }
    return res;
  }

  public void inventoryRemoved(@Nonnull ItemConduit itemConduit, @Nonnull BlockPos pos) {
    List<NetworkedInventory> invs = getOrCreate(pos);
    NetworkedInventory remove = null;
    for (NetworkedInventory ni : invs) {
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

  private IItemHandler getTargetInventory(Target target) {
    if (target.inv != null) {
      return target.inv.getInventory();
    }
    return null;
  }

  public List<String> getTargetsForExtraction(@Nonnull BlockPos extractFrom, @Nonnull IItemConduit con, @Nonnull ItemStack input) {
    List<String> result = new ArrayList<String>();

    List<NetworkedInventory> invs = getOrCreate(extractFrom);
    for (NetworkedInventory source : invs) {

      if (source.getCon().getBundle().getLocation().equals(con.getBundle().getLocation())) {
        List<Target> sendPriority = source.getSendPriority();
        if (sendPriority != null) {
          for (Target t : sendPriority) {
            IItemFilter f = t.inv.getCon().getOutputFilter(t.inv.getConDir());
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
    for (NetworkedInventory inv : inventories) {
      if (inv.hasTarget(con, dir)) {
        IItemFilter f = inv.getCon().getInputFilter(inv.getConDir());
        if (input.isEmpty() || f == null || f.doesItemPassFilter(inv.getInventory(), input)) {
          result.add(inv.getLocalizedInventoryName() + " " + inv.getLocation().toString());
        }
      }
    }
    return result;
  }

  @Override
  public void tickEnd(ServerTickEvent event, @Nullable Profiler profiler) {
    for (NetworkedInventory ni : inventories) {
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

  public List<NetworkedInventory> getInventoryPanelSources() {
    return Collections.emptyList();
  }

  // TODO make all of this actually do stuff
  public IInventoryDatabaseServer getDatabase() {
    return new IInventoryDatabaseServer() {

      @Override
      public int getGeneration() {
        return 0;
      }

      @Override
      public IServerItemEntry lookupItem(ItemStack stack, IServerItemEntry hint, boolean create) {
        return null;
      }

      @Override
      public IServerItemEntry getItem(int dbID) {
        return null;
      }

      @Override
      public IServerItemEntry getExistingItem(int dbID) {
        return null;
      }

      @Override
      public boolean isCurrent() {
        return false;
      }

      @Override
      public void addChangeLog(ChangeLog cl) {

      }

      @Override
      public void removeChangeLog(ChangeLog cl) {

      }

      @Override
      public List<? extends IServerItemEntry> decompressMissingItems(byte[] compressed) throws IOException {
        return null;
      }

      @Override
      public byte[] compressItemInfo(List<? extends IServerItemEntry> items) throws IOException {
        return null;
      }

      @Override
      public byte[] compressItemList() throws IOException {
        return null;
      }

      @Override
      public byte[] compressChangedItems(Collection<? extends IServerItemEntry> items) throws IOException {
        return null;
      }

      @Override
      public void resetDatabase() {

      }

      @Override
      public int getNumInventories() {
        return 0;
      }

      @Override
      public float getPower() {
        return 0;
      }

      @Override
      public void addPower(float power) {

      }

      @Override
      public boolean isOperational() {
        return false;
      }

      @Override
      public int extractItems(IServerItemEntry entry, int count, IInventoryPanel te) {
        return 0;
      }

      @Override
      public void tick() {
      }

      @Override
      public void sendChangeLogs() {
      }

      @Override
      public void onNeighborChange(BlockPos neighborPos) {
      }

      @Override
      public void entryChanged(IServerItemEntry entry) {
      }
    };
  }
}
