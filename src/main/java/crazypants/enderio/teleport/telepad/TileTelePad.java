package crazypants.enderio.teleport.telepad;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;

import com.google.common.collect.Lists;

import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.util.BlockCoord;
import crazypants.util.Util;

public class TileTelePad extends TileTravelAnchor implements IInternalPowerReceiver {

  boolean inNetwork;

  private EnumSet<ForgeDirection> connections = EnumSet.noneOf(ForgeDirection.class);

  private EnergyStorage energy = new EnergyStorage(100000, 1000, 1000);

  private TileTelePad master = null;

  private boolean autoUpdate = false;

  @Override
  public void updateEntity() {
    super.updateEntity();
    // my master is gone!
    if(master != null && master.isInvalid()) {
      master.breakNetwork();
    }
    if(autoUpdate && worldObj != null) {
      updateConnectedState(true);
      autoUpdate = false;
    }
  }

  public void updateConnectedState(boolean fromBlock) {

    for (BlockCoord bc : getSurroundingCoords()) {
      TileEntity te = bc.getTileEntity(worldObj);
      ForgeDirection con = Util.getDirFromOffset(xCoord - bc.x, 0, zCoord - bc.z);
      if(te instanceof TileTelePad) {
        if(fromBlock) {
          ((TileTelePad) te).updateConnectedState(false);
        }
        if(con != ForgeDirection.UNKNOWN && !((TileTelePad) te).inNetwork) {
          connections.add(con);
        }
      } else {
        connections.remove(con);
        if(master == this) {
          breakNetwork();
          updateBlock();
        } else if(con != ForgeDirection.UNKNOWN) {
          if(inNetwork && master != null && fromBlock) {
            master.updateConnectedState(false);
          }
        }
        if(te != null) {
          // manually call this to avoid infinite recursion
          worldObj.getBlock(te.xCoord, te.yCoord, te.zCoord).onNeighborChange(worldObj, te.xCoord, te.yCoord, te.zCoord, xCoord, yCoord, zCoord);
        }
      }
    }
    if(isMaster() && !inNetwork) {
      inNetwork = formNetwork();
      updateBlock();
      if(inNetwork) {
        master = this;
      }
    }
  }

  public boolean isMaster() {
    return connections.size() == 4;
  }

  private boolean formNetwork() {
    List<TileTelePad> temp = Lists.newArrayList();
    if(isMaster()) {
      for (BlockCoord c : getSurroundingCoords()) {
        TileEntity te = c.getTileEntity(worldObj);
        if(!(te instanceof TileTelePad)) {
          return false;
        }
        temp.add((TileTelePad) te);
      }
      for (TileTelePad te : temp) {
        te.master = this;
        te.inNetwork = true;
        te.updateBlock();
      }
      return true;
    }
    return false;
  }

  private void breakNetwork() {
    master = null;
    inNetwork = false;
    for (BlockCoord c : getSurroundingCoords()) {
      TileEntity te = c.getTileEntity(worldObj);
      if(te instanceof TileTelePad) {
        ((TileTelePad) te).master = null;
        ((TileTelePad) te).inNetwork = false;
        ((TileTelePad) te).updateBlock();
      }
    }
  }

  private List<BlockCoord> getSurroundingCoords() {
    List<BlockCoord> ret = Lists.newArrayList();
    for (int x = -1; x <= 1; x++) {
      for (int z = -1; z <= 1; z++) {
        if(x != 0 || z != 0) {
          ret.add(new BlockCoord(xCoord + x, yCoord, zCoord + z));
        }
      }
    }
    return ret;
  }

  @Override
  public boolean canUpdate() {
    return true;
  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    super.writeCustomNBT(root);
    energy.writeToNBT(root);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    super.readCustomNBT(root);
    energy.readFromNBT(root);
    autoUpdate = true;
  }

  /* ITravelAccessable overrides */

  @Override
  public boolean canSeeBlock(EntityPlayer playerName) {
    return isMaster() && inNetwork && getEnergyStored() > 0;
  }

  /* IInternalPowerReceiver */

  @Override
  public int getMaxEnergyRecieved(ForgeDirection dir) {
    return inNetwork && master != null ? master == this ? energy.getMaxReceive() : master.getMaxEnergyRecieved(dir) : 0;
  }

  @Override
  public int getMaxEnergyStored() {
    return inNetwork && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }

  @Override
  public boolean displayPower() {
    return inNetwork && master != null;
  }

  @Override
  public int getEnergyStored() {
    return inNetwork && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public void setEnergyStored(int storedEnergy) {
    if(inNetwork && master != null) {
      if(master == this) {
        energy.setEnergyStored(storedEnergy);
      } else {
        master.setEnergyStored(storedEnergy);
      }
    }
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return inNetwork && master != null;
  }

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return inNetwork && master != null ? master == this ? energy.receiveEnergy(maxReceive, simulate) : master.receiveEnergy(from, maxReceive, simulate) : 0;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return inNetwork && master != null ? master == this ? energy.getEnergyStored() : master.getEnergyStored() : 0;
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return inNetwork && master != null ? master == this ? energy.getMaxEnergyStored() : master.getMaxEnergyStored() : 0;
  }
}
