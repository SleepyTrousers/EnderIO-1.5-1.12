package crazypants.enderio.conduits.conduit.item;

import java.util.ArrayList;
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

  private final @Nonnull List<NetworkedInventory> inventories = new ArrayList<NetworkedInventory>();
  private final @Nonnull Map<BlockPos, List<NetworkedInventory>> invMap = new HashMap<BlockPos, List<NetworkedInventory>>();
  private final @Nonnull Map<BlockPos, IItemConduit> conMap = new HashMap<BlockPos, IItemConduit>();

  private boolean requiresSort = true;

  public ItemConduitNetwork() {
    super(IItemConduit.class, IItemConduit.class);
  }

  @Override
  public void addConduit(@Nonnull IItemConduit con) {
    super.addConduit(con);
    conMap.put(con.getBundle().getLocation(), con);

    TileEntity te = con.getBundle().getEntity();
    for (EnumFacing direction : con.getExternalConnections()) {
      if (direction != null) {
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

  public @Nullable NetworkedInventory getInventory(@Nonnull IItemConduit conduit, @Nonnull EnumFacing dir) {
    for (NetworkedInventory inv : inventories) {
      if (inv.getCon() == conduit && inv.getConDir() == dir) {
        return inv;
      }
    }
    return null;
  }

  private @Nonnull List<NetworkedInventory> getOrCreate(@Nonnull BlockPos pos) {
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

  private @Nullable IItemHandler getTargetInventory(Target target) {
    if (target.inv != null) {
      return target.inv.getInventory();
    }
    return null;
  }

  public @Nonnull List<String> getTargetsForExtraction(@Nonnull BlockPos extractFrom, @Nonnull IItemConduit con, @Nonnull ItemStack input) {
    List<String> result = new ArrayList<String>();

    List<NetworkedInventory> invs = getOrCreate(extractFrom);
    for (NetworkedInventory source : invs) {

      if (source.getCon().getBundle().getLocation().equals(con.getBundle().getLocation())) {
        List<Target> sendPriority = source.getSendPriority();
        for (Target t : sendPriority) {
          IItemFilter f = t.inv.getCon().getOutputFilter(t.inv.getConDir());
          if (input.isEmpty() || f == null || f.doesItemPassFilter(getTargetInventory(t), input)) {
            String s = t.inv.getLocalizedInventoryName() + " " + t.inv.getLocation().toString() + " Distance [" + t.distance + "] ";
            result.add(s);
          }
        }
      }
    }

    return result;
  }

  public @Nonnull List<String> getInputSourcesFor(@Nonnull IItemConduit con, @Nonnull EnumFacing dir, @Nonnull ItemStack input) {
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
    if (requiresSort) {
      for (NetworkedInventory ni : inventories) {
        Prof.start(profiler, "updateInsertOrder_", ni.getInventory());
        ni.updateInsertOrder();
        Prof.stop(profiler);
      }
      requiresSort = false;
    }
    for (NetworkedInventory ni : inventories) {
      if (ni.shouldTick()) {
        Prof.start(profiler, "NetworkedInventoryTick_", ni.getInventory());
        ni.onTick();
        Prof.stop(profiler);
      }
    }
  }

  public @Nonnull Map<BlockPos, IItemConduit> getConMap() {
    return conMap;
  }

  public @Nonnull List<NetworkedInventory> getInventories() {
    return inventories;
  }

}
