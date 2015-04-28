package crazypants.enderio.machine.wireless;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import crazypants.util.BlockCoord;
import java.util.Collections;
import java.util.Comparator;

public class WirelessChargedLocation {

  private final TileEntity te;
  private int lastChangeCount;
  private final List<IWirelessCharger> chargers;

  public WirelessChargedLocation(TileEntity te) {
    this.te = te;
    this.chargers = new ArrayList<IWirelessCharger>();
    updateChargers();
  }

  private void updateChargers() {
    WirelessChargerController wcc = WirelessChargerController.instance;
    chargers.clear();
    lastChangeCount = wcc.getChangeCount();
    final BlockCoord bc = new BlockCoord(te);
    wcc.getChargers(te.getWorldObj(), bc, chargers);
    Collections.sort(chargers, new Comparator<IWirelessCharger>() {
      @Override
      public int compare(IWirelessCharger o1, IWirelessCharger o2) {
        int dist1 = o1.getLocation().distanceSquared(bc);
        int dist2 = o2.getLocation().distanceSquared(bc);
        return dist1 - dist2;
      }
    });
  }

  private void checkChangeCount() {
    if(lastChangeCount != WirelessChargerController.instance.getChangeCount()) {
      updateChargers();
    }
  }

  public boolean chargeItems(ItemStack[] items) {
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
