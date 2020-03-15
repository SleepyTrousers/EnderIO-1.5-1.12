package crazypants.enderio.powertools.machine.capbank.network;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.enderio.core.common.BlockEnder;
import com.enderio.core.common.util.NNList;
import com.enderio.core.common.util.NNList.NNIterator;
import com.enderio.core.common.util.NullHelper;

import crazypants.enderio.powertools.machine.capbank.TileCapBank;
import crazypants.enderio.util.FuncUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class NetworkUtil {

  private static AtomicInteger nextID = new AtomicInteger((int) (Math.random() * 1000));

  public static void ensureValidNetwork(TileCapBank cap) {
    if (!reuseNetwork(cap)) {
      new CapBankNetwork(nextID.getAndIncrement()).init(cap);
    }
  }

  public static boolean hasNeigbours(TileCapBank cap) {
    if (!cap.getType().isMultiblock()) {
      return false;
    }
    for (EnumFacing dir : EnumFacing.values()) {
      TileEntity te = cap.getWorld().getTileEntity(cap.getPos().offset(NullHelper.notnullJ(dir, "Enum.values()")));
      if (te instanceof TileCapBank) {
        TileCapBank neighbour = (TileCapBank) te;
        if (neighbour.canConnectTo(cap)) {
          return true;
        }
      }
    }
    return false;
  }

  private static Collection<TileCapBank> getNeigbours(TileCapBank cap) {
    if (!cap.getType().isMultiblock()) {
      return Collections.emptyList();
    }
    Collection<TileCapBank> res = new ArrayList<TileCapBank>();
    getNeigbours(cap, res);
    return res;
  }

  private static void getNeigbours(TileCapBank cap, Collection<TileCapBank> res) {
    for (EnumFacing dir : EnumFacing.values()) {
      TileEntity te = cap.getWorld().getTileEntity(cap.getPos().offset(NullHelper.notnullJ(dir, "Enum.values()")));
      if (te instanceof TileCapBank) {
        TileCapBank neighbour = (TileCapBank) te;
        if (neighbour.canConnectTo(cap)) {
          res.add(neighbour);
        }
      }
    }
  }

  public static Collection<TileCapBank> getAllNeigbours(TileCapBank cap) {
    if (!cap.getType().isMultiblock()) {
      return Collections.singleton(cap);
    }
    Set<TileCapBank> result = new HashSet<>();
    Set<BlockPos> seen = new HashSet<>();
    List<BlockPos> todo = new ArrayList<>();
    todo.add(cap.getLocation());
    while (!todo.isEmpty()) {
      TileCapBank te = FuncUtil.runIf(todo.remove(0), next -> BlockEnder.getAnyTileEntitySafe(cap.getWorld(), next, TileCapBank.class));
      if (te != null && te.canConnectTo(cap)) {
        result.add(te);
        for (NNIterator<EnumFacing> itr = NNList.FACING.fastIterator(); itr.hasNext();) {
          BlockPos candidate = te.getPos().offset(itr.next());
          if (!seen.contains(candidate)) {
            seen.add(candidate);
            todo.add(candidate);
          }
        }
      }
    }
    return result;
  }

  private static boolean reuseNetwork(TileCapBank cap) {
    ICapBankNetwork network = null;
    for (TileCapBank conduit : getNeigbours(cap)) {
      if (network == null) {
        network = conduit.getNetwork();
      } else if (network != conduit.getNetwork()) {
        return false;
      }
    }
    if (network == null) {
      return false;
    }
    cap.setNetwork(network);
    network.addMember(cap);
    // network.notifyNetworkOfUpdate();
    return true;
  }

}
