package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.machine.wireless.WirelessChargedLocation;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.EnumSkyBlock;

public class TileElectricLight extends TileEntityEio implements IInternalPowerReceiver {

  private EnumFacing face = EnumFacing.DOWN;

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

  public EnumFacing getFace() {
    return face;
  }

  public void setFace(EnumFacing face) {
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
      
      worldObj.setBlockState(pos, getBlockType().getStateFromMeta(isActivated ? 1 : 0), 2);

      if(requiresPower) {
        for (TileLightNode ln : lightNodes) {
          if(ln != null) {
            worldObj.setBlockState(ln.getPos(), getBlockType().getStateFromMeta(isActivated ? 1 : 0), 2);            
            worldObj.markBlockForUpdate(ln.getPos());
            worldObj.checkLightFor(EnumSkyBlock.BLOCK, ln.getPos());            
          }
        }
      }
      worldObj.markBlockForUpdate(pos);
      worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);      
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
    Set<BlockPos> before;
    if(lightNodes != null && !lightNodes.isEmpty()) {
      before = new HashSet<BlockPos>(lightNodes.size());
      for (TileLightNode node : lightNodes) {
        before.add(node.getPos());
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
          TileEntity te = worldObj.getTileEntity(new BlockPos(lightNodeCoords[i], lightNodeCoords[i + 1], lightNodeCoords[i + 2]));
          if(te instanceof TileLightNode) {
            lightNodes.add((TileLightNode) te);
          }
        }
        lightNodeCoords = null;

      } else if(lightNodes == null) { // just created

        lightNodes = new ArrayList<TileLightNode>();

      }

      for (EnumFacing dir : EnumFacing.VALUES) {
        if(dir != face && dir != face.getOpposite()) { // don't project behind
          // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }

      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      Vector3d[] diags = new Vector3d[2];
      if(face.getFrontOffsetX() != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.SOUTH);
      } else if(face.getFrontOffsetY() != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.EAST);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.SOUTH);
      } else {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.EAST);
      }
      addDiaganals(diags, new Vector3d(), after);
      addDiaganals(diags, ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      if(!before.equals(after)) {
        clearLightNodes();

        for (BlockCoord entry : after) {
          worldObj.setBlockState(entry.getBlockPos(), EnderIO.blockLightNode.getDefaultState(), 3);
          TileEntity te = worldObj.getTileEntity(entry.getBlockPos());
          if(te instanceof TileLightNode) {
            TileLightNode ln = (TileLightNode) te;
            ln.setPos(getPos());            
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
    return worldObj.getBlockState(
        new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ()  + (int) offset.z)).getBlock() == EnderIO.blockLightNode;
  }

  private void clearLightNodes() {
    if(lightNodes != null) {
      for (TileLightNode ln : lightNodes) {
        if(worldObj.getBlockState(ln.getPos()).getBlock() == EnderIO.blockLightNode) {
          worldObj.setBlockToAir(ln.getPos());
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, Set<BlockCoord> result) {
    int x = getPos().getX() + (int) offset.x;
    int y = getPos().getY()+ (int) offset.y;
    int z = getPos().getZ() + (int) offset.z;

    if(isLightNode(offset)) {
      TileLightNode te = (TileLightNode) worldObj.getTileEntity(new BlockPos(x, y, z));
      if(te.parentX != getPos().getX() || te.parentY != getPos().getY() || te.parentZ != getPos().getZ()) {
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
    Block id = worldObj.getBlockState(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z)).getBlock();
    if(isRailcraftException(id)) {
      return false;
    }
    return worldObj.getBlockLightOpacity(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z)) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return worldObj.isAirBlock(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z)) || isLightNode(offset);
  }

  @Override
  public void readCustomNBT(NBTTagCompound root) {

    face = EnumFacing.values()[root.getShort("face")];
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
        lnLoc[index++] = ln.getPos().getX();
        lnLoc[index++] = ln.getPos().getY();
        lnLoc[index++] = ln.getPos().getZ();
      }
      root.setIntArray("lightNodes", lnLoc);
    }
  }

  public boolean hasPower() {
    return energyStoredRF >= RF_USE_PER_TICK;
  }

  // RF Power

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if(!requiresPower) {
      return 0;
    }
    if (energyStoredRF == 0) {
      init = true;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public boolean canConnectEnergy(EnumFacing from) {
    return requiresPower;
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

  @Override
  public BlockCoord getLocation() {
    return new BlockCoord(pos);
  }
}
