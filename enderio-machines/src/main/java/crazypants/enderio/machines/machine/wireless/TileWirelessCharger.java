package crazypants.enderio.machines.machine.wireless;

import javax.annotation.Nonnull;

import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.vecmath.Vector4f;

import crazypants.enderio.base.Log;
import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.paint.IPaintable;
import crazypants.enderio.base.paint.YetaUtil;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalRecieverTileWrapper;
import crazypants.enderio.base.power.wireless.IWirelessCharger;
import crazypants.enderio.base.power.wireless.WirelessChargerController;
import crazypants.enderio.base.render.ranged.IRanged;
import crazypants.enderio.base.render.ranged.RangeParticle;
import crazypants.enderio.machines.capacitor.CapacitorKey;
import info.loenwind.autosave.annotations.Storable;
import info.loenwind.autosave.annotations.Store;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Storable
public class TileWirelessCharger extends TileEntityEio implements ILegacyPoweredTile.Receiver, IWirelessCharger, IPaintable.IPaintableTileEntity, IRanged {

  @Store
  private int storedEnergyRF;

  private boolean registered = false;

  private @Nonnull BoundingBox bb = BoundingBox.UNIT_CUBE;
  private IBlockState blockState = null;

  public TileWirelessCharger() {
    addICap(CapabilityEnergy.ENERGY, facing -> InternalRecieverTileWrapper.get(this, facing));
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
      disableTicking();
    }
  }

  @Override
  public boolean chargeItems(NonNullList<ItemStack> items) {
    boolean chargedItem = false;
    int available = Math.min(CapacitorKey.WIRELESS_POWER_OUTPUT.getDefault(), storedEnergyRF);
    for (int i = 0, end = items.size(); i < end && available > 0; i++) {
      ItemStack item = items.get(i);
      if (!item.isEmpty()) {
        IEnergyStorage chargable = PowerHandlerUtil.getCapability(item, null);
        if (chargable != null && item.getCount() == 1) {
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
    return CapacitorKey.WIRELESS_POWER_INTAKE.getDefault();
  }

  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyStored() {
    return CapacitorKey.WIRELESS_POWER_BUFFER.getDefault();
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
    int prev = storedEnergyRF;
    storedEnergyRF = Math.max(0, storedEnergyRF - max);
    return prev - storedEnergyRF;
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return from == EnumFacing.DOWN || !((BlockNormalWirelessCharger) world.getBlockState(pos).getBlock()).isAntenna();
  }

  @Override
  public @Nonnull World getworld() {
    return getWorld();
  }

  @Override
  public boolean displayPower() {
    return true;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  @Nonnull
  @Override
  public BlockPos getLocation() {
    return pos;
  }

  @Override
  @Nonnull
  public BoundingBox getRange() {
    if (this.isInvalid()) {
      Log.error("TileEntity " + this + " at " + pos + " is invalid but was not invalidated. This should not be possible!");
      return bb;
    }
    IBlockState actualState = world.getBlockState(pos).getActualState(world, pos);
    if (actualState != blockState) {
      if (!(actualState.getBlock() instanceof BlockNormalWirelessCharger)) {
        Log.error("TileEntity " + this + " at " + pos + " is assigned to a wrong block (" + actualState
            + "). This should not be possible unless the world was severly corrupted!");
        world.removeTileEntity(pos);
        world.createExplosion(null, pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5, 1, false);
        return bb;
      }
      blockState = actualState;
      bb = ((BlockNormalWirelessCharger) actualState.getBlock()).getChargingStrength(actualState, pos);
    }
    return bb;
  }

  private boolean showingRange = false;
  private final static Vector4f color = new Vector4f(0x6a / 255f, 0x3d / 255f, 0x7d / 255f, .4f);

  protected void toggleRange() {
    showingRange = !showingRange;
    if (world.isRemote && showingRange) {
      spawnRangeParticle();
    }
  }

  @SideOnly(Side.CLIENT)
  private void spawnRangeParticle() {
    Minecraft.getMinecraft().effectRenderer.addEffect(new RangeParticle<TileWirelessCharger>(this, color));
  }

  @SideOnly(Side.CLIENT)
  @Override
  public boolean isShowingRange() {
    return showingRange;
  }

  @Override
  @Nonnull
  public BoundingBox getBounds() {
    return getRange();
  }

}
