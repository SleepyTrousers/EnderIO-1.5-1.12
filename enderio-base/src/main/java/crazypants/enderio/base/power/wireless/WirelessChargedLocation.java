package crazypants.enderio.base.power.wireless;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.NonNullList;

public class WirelessChargedLocation implements Comparator<IWirelessCharger> {

  private final @Nonnull TileEntity te;

  @Override
  public int compare(IWirelessCharger o1, IWirelessCharger o2) {
    return Double.compare(o1.getLocation().distanceSq(te.getPos()), o2.getLocation().distanceSq(te.getPos()));
  }

  public WirelessChargedLocation(@Nonnull TileEntity te) {
    this.te = te;
  }

  private @Nonnull List<IWirelessCharger> getChargers() {
    List<IWirelessCharger> chargers = new ArrayList<>();
    WirelessChargerController.getChargers(te.getWorld(), te.getPos(), chargers);
    if (chargers.size() > 1) {
      chargers.sort(this);
    }
    return chargers;
  }

  public boolean chargeItems(@Nonnull NonNullList<ItemStack> items) {
    for (IWirelessCharger wc : getChargers()) {
      if (wc.chargeItems(items) && wc.forceSingle()) {
        return true;
      }
    }
    return false;
  }

  public int takeEnergy(int max) {
    int charged = 0;
    for (IWirelessCharger wc : getChargers()) {
      charged += wc.takeEnergy(max - charged);
      if (charged >= max) {
        break;
      }
    }
    return charged;
  }
}
