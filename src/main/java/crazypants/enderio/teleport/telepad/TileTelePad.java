package crazypants.enderio.teleport.telepad;

import java.util.EnumSet;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.api.energy.EnergyStorage;

import com.google.common.collect.Lists;

import crazypants.enderio.api.teleport.ITelePad;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.teleport.anchor.TileTravelAnchor;
import crazypants.util.BlockCoord;
import crazypants.util.Util;

public class TileTelePad extends TileTravelAnchor implements IInternalPowerReceiver, ITelePad {

  private boolean inNetwork;

  private EnumSet<ForgeDirection> connections = EnumSet.noneOf(ForgeDirection.class);

  private EnergyStorage energy = new EnergyStorage(100000, 1000, 1000);

  private TileTelePad master = null;

  private boolean autoUpdate = false;

  private BlockCoord target = new BlockCoord();

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
    if (isMaster() && !worldObj.isRemote) {
      System.out.println(inNetwork() + "  " + this);
    }
  }

  public void updateConnectedState(boolean fromBlock) {

    for (BlockCoord bc : getSurroundingCoords()) {
      TileEntity te = bc.getTileEntity(worldObj);
      ForgeDirection con = Util.getDirFromOffset(xCoord - bc.x, 0, zCoord - bc.z);
      if(te instanceof TileTelePad) {
        // let's find the master and let him do the work
        if(((TileTelePad) te).isMaster() && fromBlock) {
          ((TileTelePad) te).updateConnectedState(false);
          return;
        }
        // otherwise we either are the master or this is a secondary call, so update connections
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
    target.writeToNBT(root);
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    super.readCustomNBT(root);
    energy.readFromNBT(root);
    target = new BlockCoord().readFromNBT(root);
    autoUpdate = true;
  }

  @Override
  public Packet getDescriptionPacket() {
    S35PacketUpdateTileEntity pkt = (S35PacketUpdateTileEntity) super.getDescriptionPacket();
    pkt.func_148857_g().setBoolean("inNetwork", inNetwork);
    return pkt;
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    this.inNetwork = pkt.func_148857_g().getBoolean("inNetwork");
  }

  /* ITelePad */

  @Override
  public boolean isMaster() {
    return connections.size() == 4;
  }

  @Override
  public TileTelePad getMaster() {
    return master;
  }

  @Override
  public boolean inNetwork() {
    return inNetwork;
  }

  @Override
  public int getX() {
    if (inNetwork) {
      return master.target.x;
    }
    return target.x;
  }

  @Override
  public int getY() {
    if (inNetwork) {
      return master.target.y;
    }
    return target.y;
  }

  @Override
  public int getZ() {
    if (inNetwork) {
      return master.target.z;
    }
    return target.z;
  }

  @Override
  public ITelePad setX(int x) {
    if(inNetwork()) {
      target.x = x;
      return master;
    }
    return null;
  }

  @Override
  public ITelePad setY(int y) {
    if(inNetwork()) {
      target.y = y;
      return master;
    }
    return null;
  }

  @Override
  public ITelePad setZ(int z) {
    if(inNetwork()) {
      target.z = z;
      return master;
    }
    return null;
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
