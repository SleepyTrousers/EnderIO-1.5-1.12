package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerProvider;
import buildcraft.api.power.IPowerReceptor;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.EnderPowerProvider;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileEntitySolarPanel extends TileEntity implements IInternalPowerReceptor, IPowerReceptor {

  protected EnderPowerProvider powerHandler;
  private BasicCapacitor capacitor;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private float energyPerTick = 1;

  public TileEntitySolarPanel() {
    // TODO:
    capacitor = new BasicCapacitor();
    powerHandler = PowerHandlerUtil.createHandler(capacitor);
  }

  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  @Override
  public void applyPerdition() {
  }

  @Override
  public EnderPowerProvider getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void updateEntity() {
    if (worldObj == null || worldObj.isRemote) {
      return;
    }
    collectEnergy();
    transmitEnergy();
  }

  private void collectEnergy() {
    if (!worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
      return;
    }
    float fromSun = calculateLightRatio();
    float collected = energyPerTick * fromSun;
    powerHandler.setEnergy(Math.min(powerHandler.getMaxEnergyStored(), powerHandler.getEnergyStored() + collected));

  }

  private float calculateLightRatio() {
    int lightValue = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
    float sunAngle = worldObj.getCelestialAngleRadians(1.0F);

    if (sunAngle < (float) Math.PI) {
      sunAngle += (0.0F - sunAngle) * 0.2F;
    } else {
      sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
    }

    lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

    lightValue = MathHelper.clamp_int(lightValue, 0, 15);
    return lightValue / 15f;
  }

  private boolean transmitEnergy() {

    if (powerHandler.getEnergyStored() <= 0) {
      // powerHandler.update();
      return false;
    }

    // Mandatory power handler update
    // float stored = powerHandler.getEnergyStored();
    // powerHandler.update();
    // powerHandler.setEnergy(stored);

    float canTransmit = Math.min(powerHandler.getEnergyStored(), capacitor.getMaxEnergyExtracted());
    float transmitted = 0;

    checkReceptors();

    if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      IPowerProvider pp = receptor.receptor.getPowerProvider();
      if (pp != null && pp.getMinEnergyReceived() <= canTransmit) {
        float used;
        if (receptor.receptor instanceof IInternalPowerReceptor) {
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, canTransmit, receptor.fromDir);
        } else {
          used = Math.min(canTransmit, receptor.receptor.powerRequest(receptor.fromDir));
          pp.receiveEnergy(used, receptor.fromDir);
        }
        transmitted += used;
        canTransmit -= used;
      }
      if (canTransmit <= 0) {
        break;
      }

      if (!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }

    powerHandler.setEnergy(powerHandler.getEnergyStored() - transmitted);

    return transmitted > 0;

  }

  private void checkReceptors() {
    if (!receptorsDirty) {
      return;
    }
    receptors.clear();

    BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
    ForgeDirection dir = ForgeDirection.DOWN;
    BlockCoord checkLoc = bc.getLocation(dir);
    TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
    if (te instanceof IPowerReceptor) {
      IPowerReceptor rec = (IPowerReceptor) te;
      // PowerReceiver reciever = rec.getPowerReceiver(dir.getOpposite());
      // if(reciever != null) {
      receptors.add(new Receptor((IPowerReceptor) te, dir.getOpposite()));
      // }
    }

    // NB: This is to supports connections from any direction
    // BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
    // for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
    // BlockCoord checkLoc = bc.getLocation(dir);
    // TileEntity te = worldObj.getBlockTileEntity(checkLoc.x, checkLoc.y,
    // checkLoc.z);
    // if (te instanceof IPowerReceptor) {
    // IPowerReceptor rec = (IPowerReceptor) te;
    // PowerReceiver reciever = rec.getPowerReceiver(dir.getOpposite());
    // receptors.add(new Receptor((IPowerReceptor) te, dir.getOpposite()));
    // }
    // }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerReceptor receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerReceptor rec, ForgeDirection fromDir) {
      super();
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

  @Override
  public void setPowerProvider(IPowerProvider provider) {

  }

  @Override
  public IPowerProvider getPowerProvider() {
    return powerHandler;
  }

  @Override
  public void doWork() {
  }

  @Override
  public int powerRequest(ForgeDirection from) {
    return 0;
  }

}
