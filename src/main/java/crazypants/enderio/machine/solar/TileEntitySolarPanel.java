package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.config.Config;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;

public class TileEntitySolarPanel extends TileEntity implements IInternalPowerReceptor {
  
  private BasicCapacitor capacitor;

  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private float lastCollectionValue = -1;
  private int checkOffset;
  private static final int CHECK_INTERVAL = 100;
  
  private int storedEnergyRF;

  public TileEntitySolarPanel() {
    checkOffset = (int) (Math.random() * 20);
    capacitor = new BasicCapacitor(0, 10000, 10);
  }

  
  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return getMaxEnergyStored();
  }
  
  

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return capacitor.getMaxEnergyReceived();
  }


  @Override
  public int getEnergyStored() {
    return storedEnergyRF;
  }

  @Override
  public int getMaxEnergyStored() {
    return capacitor.getMaxEnergyStored();
  }


  @Override
  public void setEnergyStored(int stored) {
    storedEnergyRF = stored;    
  }


  @Override
  public void updateEntity() {
    if(worldObj == null || worldObj.isRemote) {
      return;
    }
    collectEnergy();
    transmitEnergy();
  }

  private void collectEnergy() {
    if(!worldObj.canBlockSeeTheSky(xCoord, yCoord, zCoord)) {
      return;
    }

    if(lastCollectionValue == -1 || (worldObj.getWorldTime() + checkOffset) % CHECK_INTERVAL == 0) {
      float fromSun = calculateLightRatio();
      lastCollectionValue = getEnergyPerTick() * fromSun;
    }
    float collected = lastCollectionValue;
    storedEnergyRF += collected;    
  }

  private int getEnergyPerTick() {
    int meta = getBlockMetadata();
    if(meta == 0) {
      return Config.maxPhotovoltaicOutputRF;
    }
    return Config.maxPhotovoltaicAdvancedOutputRF;
  }

  private float calculateLightRatio() {
    int lightValue = worldObj.getSavedLightValue(EnumSkyBlock.Sky, xCoord, yCoord, zCoord) - worldObj.skylightSubtracted;
    float sunAngle = worldObj.getCelestialAngleRadians(1.0F);

    if(sunAngle < (float) Math.PI) {
      sunAngle += (0.0F - sunAngle) * 0.2F;
    } else {
      sunAngle += (((float) Math.PI * 2F) - sunAngle) * 0.2F;
    }

    lightValue = Math.round(lightValue * MathHelper.cos(sunAngle));

    lightValue = MathHelper.clamp_int(lightValue, 0, 15);
    return lightValue / 15f;
  }

  private boolean transmitEnergy() {


    int canTransmit = Math.min(getEnergyStored(), capacitor.getMaxEnergyExtracted());
    int transmitted = 0;

    checkReceptors();

    if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
      receptorIterator = receptors.listIterator();
    }

    int appliedCount = 0;
    int numReceptors = receptors.size();
    while (receptorIterator.hasNext() && canTransmit > 0 && appliedCount < numReceptors) {

      Receptor receptor = receptorIterator.next();
      IPowerInterface pp = receptor.receptor;
      if(pp != null && pp.getMinEnergyReceived(receptor.fromDir.getOpposite()) <= canTransmit) {
        int used = pp.recieveEnergy(receptor.fromDir.getOpposite(), canTransmit);
        transmitted += used;
        canTransmit -= used;
      }

      if(canTransmit <= 0) {
        break;
      }

      if(!receptors.isEmpty() && !receptorIterator.hasNext()) {
        receptorIterator = receptors.listIterator();
      }
      appliedCount++;
    }

    setEnergyStored(getEnergyStored() - transmitted);    

    return transmitted > 0;

  }

  private void checkReceptors() {
    if(!receptorsDirty) {
      return;
    }
    receptors.clear();
    BlockCoord bc = new BlockCoord(xCoord, yCoord, zCoord);
    ForgeDirection dir = ForgeDirection.DOWN;
    BlockCoord checkLoc = bc.getLocation(dir);
    TileEntity te = worldObj.getTileEntity(checkLoc.x, checkLoc.y, checkLoc.z);
    IPowerInterface pi = PowerHandlerUtil.create(te);
    if(pi != null) {
      receptors.add(new Receptor(pi, dir));
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerInterface receptor;
    ForgeDirection fromDir;

    private Receptor(IPowerInterface rec, ForgeDirection fromDir) {
      this.receptor = rec;
      this.fromDir = fromDir;
    }
  }

}
