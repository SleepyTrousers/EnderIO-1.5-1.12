package crazypants.enderio.conduit.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import crazypants.enderio.conduit.AbstractConduitNetwork;
import crazypants.enderio.conduit.ConduitNetworkTickHandler;
import crazypants.enderio.conduit.ConduitNetworkTickHandler.TickListener;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.item.NetworkedInventory.Target;
import crazypants.enderio.conduit.item.filter.IItemFilter;
import crazypants.util.BlockCoord;
import crazypants.util.Lang;

public class ItemConduitNetwork extends AbstractConduitNetwork<IItemConduit, IItemConduit> {

  private long timeAtLastApply;

  final List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
  private final Map<BlockCoord, List<NetworkedInventory>> invMap = new HashMap<BlockCoord, List<NetworkedInventory>>();

  final Map<BlockCoord, IItemConduit> conMap = new HashMap<BlockCoord, IItemConduit>();

  private boolean requiresSort = true;

  private boolean doingSend = false;

  private final InnerTickHandler tickHandler = new InnerTickHandler();

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
    NetworkedInventory inv = new NetworkedInventory(this, externalInventory, itemConduit, direction, bc);
    inventories.add(inv);
    getOrCreate(bc).add(inv);
    requiresSort = true;
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
        if(source != null && source.sendPriority != null) {
          for (Target t : source.sendPriority) {
            IItemFilter f = t.inv.con.getOutputFilter(t.inv.conDir);
            if(input == null || f == null || f.doesItemPassFilter(t.inv, input)) {
              String s = Lang.localize(t.inv.getInventory().getInventoryName(), false) + " " + t.inv.location + " Distance [" + t.distance + "] ";
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
          result.add(Lang.localize(inv.getInventory().getInventoryName(), false) + " " + inv.location);
        }
      }
    }
    return result;
  }

  private boolean isRemote(ItemConduit itemConduit) {
    World world = itemConduit.getBundle().getEntity().getWorldObj();
    if(world != null && world.isRemote) {
      return true;
    }
    return false;
  }

  @Override
  public void onUpdateEntity(IConduit conduit) {
    World world = conduit.getBundle().getEntity().getWorldObj();
    if(world == null) {
      return;
    }
    if(world.isRemote) {
      return;
    }
    long curTime = world.getTotalWorldTime();
    if(curTime != timeAtLastApply) {
      timeAtLastApply = curTime;
      tickHandler.tick = world.getTotalWorldTime();
      ConduitNetworkTickHandler.instance.addListener(tickHandler);
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

  static int MAX_SLOT_CHECK_PER_TICK = 64;

  private class InnerTickHandler implements TickListener {

    long tick;

    @Override
    public void tickStart(ServerTickEvent evt) {
    }

    @Override
    public void tickEnd(ServerTickEvent evt) {
      doTick(tick);
    }
  }

}
