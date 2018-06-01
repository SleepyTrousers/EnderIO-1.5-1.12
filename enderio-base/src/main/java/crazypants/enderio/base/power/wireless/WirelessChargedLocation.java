package crazypants.enderio.base.power.wireless;

import java.util.Collections;
import java.util.Comparator;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.NNList;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;

public class WirelessChargedLocation {

  private final @Nonnull TileEntity te;
  private int lastChangeCount;
  private final @Nonnull NNList<IWirelessCharger> chargers;

  public WirelessChargedLocation(@Nonnull TileEntity te) {
    this.te = te;
    this.chargers = new NNList<IWirelessCharger>();
    /*
     * Set lastChangeCount to one less than the current change count so that the charger list is updated on first use (unless ~4B changes happen in between). Do
     * this instead directly updating so that WirelessChargedLocation can be used while the TileEntity is still in construction (or loading).
     */
    this.lastChangeCount = WirelessChargerController.instance.getChangeCount() - 1;
  }

  private void updateChargers() {
    WirelessChargerController wcc = WirelessChargerController.instance;
    chargers.clear();
    lastChangeCount = wcc.getChangeCount();
    final BlockPos bc = te.getPos();
    wcc.getChargers(te.getWorld(), bc, chargers);
    Collections.sort(chargers, new Comparator<IWirelessCharger>() {
      @Override
      public int compare(IWirelessCharger o1, IWirelessCharger o2) {
        double dist1 = o1.getRange().getCenter().squareDistanceTo(bc.getX(), bc.getY(), bc.getZ());
        double dist2 = o2.getRange().getCenter().squareDistanceTo(bc.getX(), bc.getY(), bc.getZ());
        return Double.compare(dist1, dist2);
      }
    });
  }

  private void checkChangeCount() {
    if (lastChangeCount != WirelessChargerController.instance.getChangeCount()) {
      updateChargers();
    }
  }

  public boolean chargeItems(NonNullList<ItemStack> items) {
    checkChangeCount();
    for (IWirelessCharger wc : chargers) {
      if (wc.chargeItems(items)) {
        return true;
      }
    }
    return false;
  }

  public int takeEnergy(int max) {
    checkChangeCount();
    int charged = 0;
    for (IWirelessCharger wc : chargers) {
      charged += wc.takeEnergy(max - charged);
      if (charged >= max) {
        break;
      }
    }
    return charged;
  }
}
