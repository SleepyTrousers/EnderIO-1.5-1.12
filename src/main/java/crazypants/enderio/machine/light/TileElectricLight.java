package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.machine.wireless.WirelessChargedLocation;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;

public class TileElectricLight extends TileEntityEio implements IInternalPowerReceiver {

  private ForgeDirection face = ForgeDirection.DOWN;

  public static final int RF_USE_PER_TICK = 1;

  private boolean init = true;

  private List<TileLightNode> lightNodes;

  private int[] lightNodeCoords;

  private boolean updatingLightNodes = false;

  private boolean lastActive = false;

  private boolean isInvereted;

  private boolean requiresPower = true;
  
  private WirelessChargedLocation chargedLocation;

  private int energyStoredRF;

  public TileElectricLight() {
  }

  public void onNeighborBlockChange(Block blockID) {
    init = true;
  }

  public void nodeNeighbourChanged(TileLightNode tileLightNode) {
    init = true;
  }

  public void nodeRemoved(TileLightNode tileLightNode) {
    if(!updatingLightNodes) {
      init = true;
    }
  }

  public ForgeDirection getFace() {
    return face;
  }

  public void setFace(ForgeDirection face) {
    this.face = face;
  }

  public void setInverted(boolean inverted) {
    isInvereted = inverted;
  }

  public void setRequiresPower(boolean isPowered) {
    requiresPower = isPowered;
  }
  
  public boolean isRequiresPower() {
    return requiresPower;
  }

  public void setInvereted(boolean isInvereted) {
    this.isInvereted = isInvereted;
  }
  
  public boolean isInvereted() {
    return isInvereted;
  }
  
  public void setWireless(boolean wireless) {
    if(!wireless) {
      chargedLocation = null;
    } else if(chargedLocation == null) {
      chargedLocation = new WirelessChargedLocation(this);
    }
  }

  public boolean isWireless() {
    return chargedLocation != null;
  }

  @Override
  public void doUpdate() {
    if(worldObj.isRemote) {
      return;
    }

    boolean isActivated = init ? isPoweredRedstone() ^ isInvereted : lastActive;

    if(requiresPower) {
      if(isActivated) {
        if(!hasPower()) {
          isActivated = false;
        } else {
          setEnergyStored(getEnergyStored() - RF_USE_PER_TICK);
        }
      }

      if(init) {
        updateLightNodes();
      }
    }

    if(isActivated != lastActive || init) {
      worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActivated ? 1 : 0, 2);

      if(requiresPower) {
        for (TileLightNode ln : lightNodes) {
          if(ln != null) {
            worldObj.setBlockMetadataWithNotify(ln.xCoord, ln.yCoord, ln.zCoord, isActivated ? 1 : 0, 2);
            worldObj.markBlockForUpdate(ln.xCoord, ln.yCoord, ln.zCoord);
            worldObj.updateLightByType(EnumSkyBlock.Block, ln.xCoord, ln.yCoord, ln.zCoord);
          }
        }
      }
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
      init = false;
      lastActive = isActivated;
    }
    
    if (chargedLocation != null) {
      if (energyStoredRF < getMaxEnergyStored()) {
        boolean needInit = energyStoredRF == 0;
        energyStoredRF += chargedLocation.takeEnergy(Math.min(getMaxEnergyStored() - energyStoredRF, 10));
        if (needInit && energyStoredRF > 0) {
          init = true;
        }
      }
    }
  }

  public void onBlockRemoved() {
    if(!requiresPower) {
      return;
    }
    updatingLightNodes = true;
    try {
      clearLightNodes();
    } finally {
      updatingLightNodes = false;
    }
  }

  private void updateLightNodes() {
    Set<BlockCoord> before;
    if(lightNodes != null && !lightNodes.isEmpty()) {
      before = new HashSet<BlockCoord>(lightNodes.size());
      for (TileLightNode node : lightNodes) {
        before.add(node.getLocation());
      }
    } else {
      before = Collections.emptySet();
    }
    Set<BlockCoord> after = new HashSet<BlockCoord>(17);
    updatingLightNodes = true;
    try {
      if(lightNodeCoords != null) {

        // just loaded
        lightNodes = new ArrayList<TileLightNode>();
        for (int i = 0; i < lightNodeCoords.length; i += 3) {
          TileEntity te = worldObj.getTileEntity(lightNodeCoords[i], lightNodeCoords[i + 1], lightNodeCoords[i + 2]);
          if(te instanceof TileLightNode) {
            lightNodes.add((TileLightNode) te);
          }
        }
        lightNodeCoords = null;

      } else if(lightNodes == null) { // just created

        lightNodes = new ArrayList<TileLightNode>();

      }

      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if(dir != face && dir != face.getOpposite()) { // don't project behind
          // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }

      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      Vector3d[] diags = new Vector3d[2];
      if(face.offsetX != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else if(face.offsetY != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
      }
      addDiaganals(diags, new Vector3d(), after);
      addDiaganals(diags, ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      if(!before.equals(after)) {
        clearLightNodes();

        for (BlockCoord entry : after) {
          worldObj.setBlock(entry.x, entry.y, entry.z, EnderIO.blockLightNode);
          TileEntity te = worldObj.getTileEntity(entry.x, entry.y, entry.z);
          if(te instanceof TileLightNode) {
            TileLightNode ln = (TileLightNode) te;
            ln.parentX = xCoord;
            ln.parentY = yCoord;
            ln.parentZ = zCoord;
            lightNodes.add(ln);
          }
        }

      } else {
        init = false;
      }

    } finally {
      updatingLightNodes = false;
    }
  }

  private void addDiaganals(Vector3d[] diags, Vector3d trans, Set<BlockCoord> result) {
    Vector3d offset = new Vector3d();
    offset.set(diags[0]);
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), result);

    offset.set(diags[0]);
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), result);

    offset.set(diags[0]);
    offset.negate();
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), result);

    offset.set(diags[0]);
    offset.negate();
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), result);
  }

  private void addNodeInDirection(Vector3d offset, Set<BlockCoord> result) {
    boolean isAir = isAir(offset);
    boolean isTransp = isTranparent(offset);
    if(isAir || isTransp) {
      offset.scale(2);
      if(isAir(offset)) {
        addLightNode(offset, result);
      } else if(isAir) {
        offset.scale(0.5);
        addLightNode(offset, result);
      }
    }
  }

  private boolean isLightNode(Vector3d offset) {
    return worldObj.getBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == EnderIO.blockLightNode;
  }

  private void clearLightNodes() {
    if(lightNodes != null) {
      for (TileLightNode ln : lightNodes) {
        if(worldObj.getBlock(ln.xCoord, ln.yCoord, ln.zCoord) == EnderIO.blockLightNode) {
          worldObj.setBlockToAir(ln.xCoord, ln.yCoord, ln.zCoord);
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, Set<BlockCoord> result) {
    int x = xCoord + (int) offset.x;
    int y = yCoord + (int) offset.y;
    int z = zCoord + (int) offset.z;

    if(isLightNode(offset)) {
      TileLightNode te = (TileLightNode) worldObj.getTileEntity(x, y, z);
      if(te.parentX != xCoord || te.parentY != yCoord || te.parentZ != zCoord) {
        // its somebody else's so leave it alone
        return;
      }
    }
    result.add(new BlockCoord(x, y, z));
  }

  private boolean isRailcraftException(Block id) {
    String className = id.getClass().getName();
    return className.equals("mods.railcraft.common.blocks.machine.BlockMachine");
  }

  private boolean isTranparent(Vector3d offset) {
    Block id = worldObj.getBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z);
    if(isRailcraftException(id)) {
      return false;
    }
    return worldObj.getBlockLightOpacity(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return worldObj.isAirBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) || isLightNode(offset);
  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {

    face = ForgeDirection.values()[root.getShort("face")];
    isInvereted = root.getBoolean("isInverted");
    requiresPower = root.getBoolean("requiresPower");
    setWireless(root.getBoolean("isWireless"));

    if(root.hasKey("storedEnergy")) {
      float se = root.getFloat("storedEnergy");
      energyStoredRF = (int) (se * 10);
    } else {
      energyStoredRF = root.getInteger("storedEnergyRF");
    }

    lightNodeCoords = root.getIntArray("lightNodes");
  }

  @Override
  public void writeCustomNBT(NBTTagCompound root) {

    root.setShort("face", (short) face.ordinal());
    root.setInteger("storedEnergyRF", energyStoredRF);
    root.setBoolean("isInverted", isInvereted);
    root.setBoolean("requiresPower", requiresPower);
    root.setBoolean("isWireless", isWireless());

    if(lightNodes != null) {
      int[] lnLoc = new int[lightNodes.size() * 3];
      int index = 0;
      for (TileLightNode ln : lightNodes) {
        lnLoc[index++] = ln.xCoord;
        lnLoc[index++] = ln.yCoord;
        lnLoc[index++] = ln.zCoord;
      }
      root.setIntArray("lightNodes", lnLoc);
    }
  }

  public boolean hasPower() {
    return energyStoredRF >= RF_USE_PER_TICK;
  }

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    if(!requiresPower) {
      return 0;
    }
    if (energyStoredRF == 0) {
      init = true;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public boolean canConnectEnergy(ForgeDirection from) {
    return requiresPower;
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
    if(!requiresPower) {
      return 0;
    }
    return Capacitors.BASIC_CAPACITOR.capacitor.getMaxEnergyReceived();
  }

  @Override
  public int getEnergyStored() {
    if(!requiresPower) {
      return 0;
    }
    return energyStoredRF;
  }

  @Override
  public int getMaxEnergyStored() {
    if(!requiresPower) {
      return 0;
    }
    return 100;
  }

  @Override
  public void setEnergyStored(int stored) {
    energyStoredRF = stored;
  }

  @Override
  public boolean displayPower() {
    return isRequiresPower();
  }
}
