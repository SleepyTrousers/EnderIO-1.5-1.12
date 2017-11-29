package crazypants.enderio.machines.machine.wireless;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.enderio.core.common.util.BlockCoord;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class WirelessChargedLocation {

  private final TileEntity te;
  private int lastChangeCount;
  private final List<IWirelessCharger> chargers;

  public WirelessChargedLocation(TileEntity te) {
    this.te = te;
    this.chargers = new ArrayList<IWirelessCharger>();
    /* Set lastChangeCount to one less than the current change count so that
       the charger list is updated on first use (unless ~4B changes happen in between).
       Do this instead directly updating so that WirelessChargedLocation can be used
       while the TileEntity is still in construction (or loading). */
    this.lastChangeCount = WirelessChargerController.instance.getChangeCount() - 1;
  }

  private void updateChargers() {
    WirelessChargerController wcc = WirelessChargerController.instance;
    chargers.clear();
    lastChangeCount = wcc.getChangeCount();
    final BlockPos bc = BlockCoord.get(te);
    wcc.getChargers(te.getWorld(), bc, chargers);
    Collections.sort(chargers, new Comparator<IWirelessCharger>() {
      @Override
      public int compare(IWirelessCharger o1, IWirelessCharger o2) {
        int dist1 = BlockCoord.getDistSq(o1.getLocation(), bc);
        int dist2 = BlockCoord.getDistSq(o2.getLocation(), bc);
        return dist1 - dist2;
      }
    });
  }

  private void checkChangeCount() {
    if(lastChangeCount != WirelessChargerController.instance.getChangeCount()) {
      updateChargers();
    }
  }

  public boolean chargeItems(NonNullList<ItemStack> items) {
    checkChangeCount();
    for(IWirelessCharger wc : chargers) {
      if(wc.isActive()) {
        if(wc.chargeItems(items)) {
          return true;
        }
      }
    }
    return false;
  }

  public int takeEnergy(int max) {
    checkChangeCount();
    int charged = 0;
    for(IWirelessCharger wc : chargers) {
      if(wc.isActive()) {
        charged += wc.takeEnergy(max - charged);
        if(charged >= max) {
          break;
        }
      }
    }
    return charged;
  }
}
