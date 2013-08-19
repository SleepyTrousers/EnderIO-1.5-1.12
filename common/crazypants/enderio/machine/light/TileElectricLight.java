package crazypants.enderio.machine.light;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileElectricLight extends TileEntity implements IInternalPowerReceptor {

  private ForgeDirection face = ForgeDirection.DOWN;
  
  public static final float MJ_USE_PER_TICK = 0.2f;
  
  protected PowerHandler powerHandler;
  
  private boolean init = true;
  
  
  public TileElectricLight() {
    powerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(1,6), this, Type.MACHINE);
  }
  
  public void onNeighborBlockChange(int blockID) {       
  }

  public ForgeDirection getFace() {
    return face;
  }

  public void setFace(ForgeDirection face) {
    this.face = face;
  }

  public boolean isOn() {
    return blockMetadata > 0;
  }
  
  @Override
  public void updateEntity() {
    if(worldObj.isRemote) {
      return;
    }    
    
    boolean hasRedstone = worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;    
    powerHandler.update();    
    if(hasRedstone) {
      powerHandler.setEnergy(Math.max(0,powerHandler.getEnergyStored() - MJ_USE_PER_TICK));      
    }
    
    boolean isActivated = hasPower() && hasRedstone;
    
    if(isActivated && !isOn() || !isActivated && isOn() || init) {           
      worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActivated ? 1 : 0, 2);
      init = false;
    } 
  }

  
  @Override
  public void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
    face = ForgeDirection.values()[root.getShort("face")];  
    
    float storedEnergy = root.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
  }

  @Override
  public void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    root.setShort("face", (short)face.ordinal());
    root.setFloat("storedEnergy", powerHandler.getEnergyStored());
  }
  
  public boolean hasPower() {
    boolean hasPower = powerHandler.getEnergyStored() > MJ_USE_PER_TICK;
    return hasPower;
  }
  
  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public PowerReceiver getPowerReceiver(ForgeDirection side) {
    return powerHandler.getPowerReceiver();
  }

  @Override
  public void doWork(PowerHandler workProvider) {    
  }

  @Override
  public World getWorld() {
    return worldObj;
  }

  @Override
  public PowerHandler getPowerHandler() {
    return powerHandler;
  }

  @Override
  public void applyPerdition() {    
  }
  

}
