package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.item.NetworkedInventory.Target;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.enderio.machine.invpanel.server.InventoryDatabaseServer;

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
    NetworkedInventory inv = new NetworkedInventory(this, externalInventory, itemConduit, direction, bc);
    inventories.add(inv);
    getOrCreate(bc).add(inv);
    requiresSort = true;
  }
  
  public NetworkedInventory getInventory(IItemConduit conduit, ForgeDirection dir) {
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

  public void inventoryRemoved(ItemConduit itemConduit, int x, int y, int z) {
    BlockCoord bc = new BlockCoord(x, y, z);
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

  public ItemStack sendItems(ItemConduit itemConduit, ItemStack item, ForgeDirection side) {
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
              String s = t.inv.getLocalizedInventoryName() + " " + t.inv.location.chatString() + " Distance [" + t.distance + "] ";
              result.add(s);
            }
          }
        }
      }
    }

    return result;
  }

  public List<String> getInputSourcesFor(IItemConduit con, ForgeDirection dir, ItemStack input) {
    List<String> result = new ArrayList<String>();
    for (NetworkedInventory inv : inventories) {
      if(inv.hasTarget(con, dir)) {
        IItemFilter f = inv.con.getInputFilter(inv.conDir);
        if(input == null || f == null || f.doesItemPassFilter(inv, input)) {
          result.add(inv.getLocalizedInventoryName() + " " + inv.location.chatString());
        }
      }
    }
    return result;
  }

  @Override
  public void doNetworkTick() {
    for (NetworkedInventory ni : inventories) {
      if(requiresSort) {
        ni.updateInsertOrder();
      }
      ni.onTick();
    }
    if(requiresSort) {
      requiresSort = false;
      changeCount++;
    }
    if(database != null) {
      database.tick();
    }
  }

  static int compare(int x, int y) {
    return (x < y) ? -1 : ((x == y) ? 0 : 1);
  }

  static int MAX_SLOT_CHECK_PER_TICK = 64;

}
