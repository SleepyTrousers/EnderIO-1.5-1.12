package crazypants.enderio.machine.wireless;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import crazypants.util.BlockCoord;

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
    wcc.getChargers(te.getWorldObj(), new BlockCoord(te), chargers);
  }

  public boolean chargeItems(ItemStack[] items) {
    if(lastChangeCount != WirelessChargerController.instance.getChangeCount()) {
      updateChargers();
    }
    for(IWirelessCharger wc : chargers) {
      if(wc.isActive()) {
        if(wc.chargeItems(items)) {
          return true;
        }
      }
    }
    return false;
  }
}
