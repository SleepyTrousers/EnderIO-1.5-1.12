package crazypants.enderio.machine.wireless;

import javax.annotation.Nullable;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.network.PacketHandler;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.YetaUtil;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import com.enderio.core.common.NBTAction;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.energy.IEnergyStorage;

@Storable
public class TileWirelessCharger extends TileEntityEio implements IInternalPowerReceiver, IWirelessCharger, IPaintable.IPaintableTileEntity {

  public static final int MAX_ENERGY_STORED = 200000;
  public static final int MAX_ENERGY_IN = 10000;
  public static final int MAX_ENERGY_OUT = 10000;

  @Store
  int storedEnergyRF;

  private double lastPowerUpdate = -1;

  private boolean registered = false;

  public TileWirelessCharger() {
  }

  @Override
  public void invalidate() {
    super.invalidate();
    WirelessChargerController.instance.deregisterCharger(this);
    registered = false;
  }

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      YetaUtil.refresh(this);
      return;
    }

    if (!registered) {
      WirelessChargerController.instance.registerCharger(this);
      registered = true;
    }

    if ((lastPowerUpdate == -1) || (lastPowerUpdate == 0 && storedEnergyRF > 0) || (lastPowerUpdate > 0 && storedEnergyRF == 0)
        || (lastPowerUpdate != storedEnergyRF && shouldDoWorkThisTick(20))) {
      lastPowerUpdate = storedEnergyRF;
      PacketHandler.sendToAllAround(new PacketStoredEnergy(this), this);
    }

  }

  @Override
  public boolean chargeItems(ItemStack[] items) {
    boolean chargedItem = false;
    int available = Math.min(MAX_ENERGY_OUT, storedEnergyRF);
    for (int i = 0, end = items.length; i < end && available > 0; i++) {
      ItemStack item = items[i];
      if (item != null) {
        IEnergyStorage chargable = PowerHandlerUtil.getCapability(item, null);
        if (chargable != null && item.stackSize == 1) {
          int max = chargable.getMaxEnergyStored();
          int cur = chargable.getEnergyStored();
          int canUse = Math.min(available, max - cur);
          if (cur < max) {
            int used = chargable.receiveEnergy(canUse, false);
            if (used > 0) {
              storedEnergyRF = storedEnergyRF - used;
              chargedItem = true;
              available -= used;
            }
          }
        }
      }
    }
    return chargedItem;
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return MAX_ENERGY_IN;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing facing) {
    return MAX_ENERGY_STORED;
  }

  @Override
  public void setEnergyStored(int stored) {
    storedEnergyRF = stored;
  }

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int takeEnergy(int max) {
    if (isActive()) {
      int prev = storedEnergyRF;
      storedEnergyRF = Math.max(0, storedEnergyRF - max);
      return prev - storedEnergyRF;
    }
    return 0;
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return true;
  }

  @Override
  public World getworld() {
    return getWorld();
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  @Override
  public boolean isActive() {
    return getEnergyStored(null) > 0 && !isPoweredRedstone();
  }

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }

  @Override
  protected void onAfterDataPacket() {
    updateBlock();
  }

  @Store({ NBTAction.CLIENT, NBTAction.SAVE })
  protected IBlockState sourceBlock;

  @Override
  public IBlockState getPaintSource() {
    return sourceBlock;
  }

  @Override
  public void setPaintSource(@Nullable IBlockState sourceBlock) {
    this.sourceBlock = sourceBlock;
    markDirty();
    updateBlock();
  }

}
