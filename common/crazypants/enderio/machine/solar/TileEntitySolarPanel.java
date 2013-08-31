package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.IPowerEmitter;
import buildcraft.api.power.IPowerReceptor;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileEntitySolarPanel extends TileEntity implements IInternalPowerReceptor, IPowerEmitter {

  protected PowerHandler powerHandler;
  private BasicCapacitor capacitor;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private float energyPerTick = 1;

  public TileEntitySolarPanel() {
    capacitor = new BasicCapacitor(0,10,10000);
    powerHandler = PowerHandlerUtil.createHandler(capacitor, this, Type.ENGINE);
  }

  @Override
  public boolean canEmitPowerFrom(ForgeDirection side) {
    return side == ForgeDirection.DOWN;
  }

  @Override
  public void doWork(PowerHandler workProvider) {
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return powerHandler.getPowerReceiver();
  }

  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  @Override
  public void applyPerdition() {
  }

  @Override
  public PowerHandler getPowerHandler() {
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
      powerHandler.update();
      return false;
    }

    // Mandatory power handler update
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    powerHandler.setEnergy(stored);

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
      PowerReceiver pp = receptor.receptor.getPowerReceiver(receptor.fromDir);
      if (pp != null && pp.getMinEnergyReceived() <= canTransmit && pp.getType() != Type.ENGINE) {
        float used;
        if (receptor.receptor instanceof IInternalPowerReceptor) {
          used = PowerHandlerUtil.transmitInternal((IInternalPowerReceptor) receptor.receptor, pp, canTransmit, Type.ENGINE, receptor.fromDir);
        } else {
          used = pp.receiveEnergy(Type.ENGINE, canTransmit, receptor.fromDir);
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
      PowerReceiver reciever = rec.getPowerReceiver(dir.getOpposite());
      if (reciever != null) {
        receptors.add(new Receptor((IPowerReceptor) te, dir.getOpposite()));
      }
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

}
