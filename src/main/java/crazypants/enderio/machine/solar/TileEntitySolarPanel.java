package crazypants.enderio.machine.solar;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import cofh.api.energy.EnergyStorage;

import com.enderio.core.common.util.BlockCoord;

import crazypants.enderio.TileEntityEio;
import crazypants.enderio.power.IInternalPowerProvider;
import crazypants.enderio.power.IPowerInterface;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.enderio.waila.IWailaNBTProvider;

public class TileEntitySolarPanel extends TileEntityEio implements IInternalPowerProvider, IWailaNBTProvider {
  
  private final List<Receptor> receptors = new ArrayList<Receptor>();
  private ListIterator<Receptor> receptorIterator = receptors.listIterator();
  private boolean receptorsDirty = true;

  private int lastCollectionValue = -1;
  
  private static final int CHECK_INTERVAL = 100;
  
  EnergyStorage destroyedNetworkBuffer = null;
  
  protected SolarPanelNetwork network = new SolarPanelNetwork();

  public void onNeighborBlockChange() {
    receptorsDirty = true;
  }

  

  // RF Power

  @Override
  public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return from == EnumFacing.DOWN;
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    return getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
    return getMaxEnergyStored();
  }
  
  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    return 0;
  }

  @Override
  public int getEnergyStored() {
    return network.getEnergyStored();
  }

  @Override
  public int getMaxEnergyStored() {
    return network.getMaxEnergyStored();
  }

  @Override
  public void setEnergyStored(int stored) {
    network.setEnergyStored(stored);
  }

  @Override
  public void doUpdate() {
    if (worldObj.isRemote) {
      return;
    }
    collectEnergy();
    transmitEnergy();
    
    if (network.isValid()) {
      if (destroyedNetworkBuffer != null) {
        network.addBuffer(destroyedNetworkBuffer);
        destroyedNetworkBuffer = null;
      }
      network.onUpdate(this);
    }
    
    if (!network.isValid() || (shouldDoWorkThisTick(20, 1) && network.addToNetwork(this))) {
      findNetwork();
    }
  }
  
  @Override
  public void invalidate() {
    network.removeFromNetwork(this);
    super.invalidate();
  }

  private void findNetwork() {
    for (EnumFacing dir : SolarPanelNetwork.VALID_CONS) {
      TileEntity te = new BlockCoord(this).getLocation(dir).getTileEntity(worldObj);
      if(te != null && te instanceof TileEntitySolarPanel && ((TileEntitySolarPanel) te).canConnect(this)) {
        SolarPanelNetwork network = ((TileEntitySolarPanel) te).network;
        if(network != null) {
          network.addToNetwork(this);
        }
      }
    }

    if(!network.isValid()) {
      network = new SolarPanelNetwork(this);
    }
  }

  private boolean canConnect(TileEntitySolarPanel other) {
    return getBlockMetadata() == other.getBlockMetadata();
  }

  private void collectEnergy() {
    if(canSeeSun()) {
      if(lastCollectionValue == -1 || shouldDoWorkThisTick(CHECK_INTERVAL)) {
        float fromSun = calculateLightRatio();
        lastCollectionValue = Math.round(getEnergyPerTick() * fromSun);
      }
      if(lastCollectionValue > 0) {
        network.setEnergyStored(Math.min(lastCollectionValue + network.getEnergyStored(), network.getMaxEnergyStored()));
      }
    }
  }

  private int getEnergyPerTick() {
    return this.worldObj.getBlockState(this.pos).getValue(SolarType.KIND).getRfperTick();
  }

  float calculateLightRatio() {
    return calculateLightRatio(worldObj, pos);
  }
  
  boolean canSeeSun() {
    return worldObj.canBlockSeeSky(pos) || worldObj.canBlockSeeSky(pos.up());
  }

  public static float calculateLightRatio(World world, BlockPos pos) {    
    int lightValue = Math.max(world.getLightFor(EnumSkyBlock.SKY, pos), world.getLightFor(EnumSkyBlock.SKY, pos.up())) - world.getSkylightSubtracted();
    float sunAngle = world.getCelestialAngleRadians(1.0F);

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


    int canTransmit = Math.min(getEnergyStored(), network.getMaxEnergyExtracted());
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
    BlockCoord bc = new BlockCoord(pos);
    EnumFacing dir = EnumFacing.DOWN;
    BlockCoord checkLoc = bc.getLocation(dir);
    TileEntity te = worldObj.getTileEntity(checkLoc.getBlockPos());
    IPowerInterface pi = PowerHandlerUtil.create(te);
    if(pi != null) {
      receptors.add(new Receptor(pi, dir));
    }
    receptorIterator = receptors.listIterator();
    receptorsDirty = false;

  }

  static class Receptor {
    IPowerInterface receptor;
    EnumFacing fromDir;

    private Receptor(IPowerInterface rec, EnumFacing fromDir) {
      receptor = rec;
      this.fromDir = fromDir;
    }
  }

  @Override
  public void readCustomNBT(NBTTagCompound tag) {
    network.readFromNBT(this, tag);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound tag) {
    if (network.isValid() && network.shouldSave(this)) {
      network.writeToNBT(tag);
      tag.setInteger("rfCap", network.getMaxEnergyStored()); // for WAILA
    }
  }

  @Override
  public Packet<?> getDescriptionPacket() {
    NBTTagCompound nbt = new NBTTagCompound();
    writeToNBT(nbt);
    return new S35PacketUpdateTileEntity(pos, 1, nbt);
  }
 
  @Override
  public boolean displayPower() {
    return true;
  }

  public void setNetwork(SolarPanelNetwork network) {
    this.network = network;
  }
  
  public boolean isMaster() {
    return network.getMaster() == this;
  }

  @Override
  public void getData(NBTTagCompound tag) {
    if (network.isValid()) {
      network.getMaster().writeToNBT(tag);
    }
  }

  @Override
  public BlockCoord getLocation() {    
    return new BlockCoord(pos);
  }
}
