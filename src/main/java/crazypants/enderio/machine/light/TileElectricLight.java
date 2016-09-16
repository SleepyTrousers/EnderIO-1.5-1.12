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
import crazypants.enderio.capacitor.DefaultCapacitorData;
import crazypants.enderio.machine.wireless.WirelessChargedLocation;
import crazypants.enderio.power.IInternalPowerReceiver;
import crazypants.enderio.power.PowerHandlerUtil;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.handlers.minecraft.HandleBlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;

import static crazypants.enderio.capacitor.CapacitorKey.LEGACY_ENERGY_INTAKE;

public class TileElectricLight extends TileEntityEio implements IInternalPowerReceiver {

  @Store
  private EnumFacing face = EnumFacing.DOWN;

  public static final int RF_USE_PER_TICK = 1;

  private boolean init = true;

  @Store(handler = HandleBlockPos.HandleBlockPosList.class)
  private List<BlockPos> lightNodes;

  private boolean updatingLightNodes = false;

  private boolean lastActive = false;

  @Store
  private boolean isInvereted;

  @Store
  private boolean isWireless;

  @Store
  private boolean requiresPower = true;
  
  private WirelessChargedLocation chargedLocation;

  @Store
  private int energyStoredRF;

  public TileElectricLight() {
  }

  public void onNeighborBlockChange(Block blockID) {
    if (!updatingLightNodes) {
      init = true;
    }
  }

  public void nodeNeighbourChanged(TileLightNode tileLightNode) {
    if (!updatingLightNodes) {
      init = true;
    }
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
    this.isWireless = wireless;
  }

  public boolean isWireless() {
    return isWireless;
  }

  @Override
  public void doUpdate() {
    if(worldObj.isRemote) {
      return;
    }

    boolean isActivated = init ? worldObj.isBlockPowered(pos) ^ isInvereted : lastActive;
    if(requiresPower) {
      if(isActivated) {
        if(!hasPower()) {
          isActivated = false;
        } else {
          setEnergyStored(getEnergyStored(null) - RF_USE_PER_TICK);
        }
      }

      if(init) {
        updateLightNodes();
      }
    }

    if(isActivated != lastActive || init) {

      IBlockState bs = worldObj.getBlockState(pos);
      bs = bs.withProperty(BlockElectricLight.ACTIVE, isActivated);
      worldObj.setBlockState(pos, bs, 2);

      if(requiresPower) {
        for (BlockPos ln : lightNodes) {
          if(ln != null) {
            bs = worldObj.getBlockState(ln);
            if(bs.getBlock() == EnderIO.blockLightNode) {
              bs = bs.withProperty(BlockLightNode.ACTIVE, isActivated);
              worldObj.setBlockState(ln, bs, 2);
              worldObj.notifyBlockUpdate(ln, bs, bs, 3);
              worldObj.checkLightFor(EnumSkyBlock.BLOCK, ln);
            }
          }
        }
      }
      worldObj.notifyBlockUpdate(pos, bs, bs, 3);
      worldObj.checkLightFor(EnumSkyBlock.BLOCK, pos);
      init = false;
      lastActive = isActivated;
      
    }

    if (isWireless) {
      if (chargedLocation == null) {
        chargedLocation = new WirelessChargedLocation(this);
      }
      if (energyStoredRF < getMaxEnergyStored(null)) {
        boolean needInit = energyStoredRF == 0;
        energyStoredRF += chargedLocation.takeEnergy(Math.min(getMaxEnergyStored(null) - energyStoredRF, 10));
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
      for (BlockPos node : lightNodes) {
        before.add(node);
      }
    } else {
      before = Collections.emptySet();
    }
    Set<BlockPos> after = new HashSet<BlockPos>(17);
    updatingLightNodes = true;
    try {
      if (lightNodes == null) { // just created
        lightNodes = new ArrayList<BlockPos>();
      }

      for (EnumFacing dir : EnumFacing.VALUES) {
        if(dir != face && dir != face.getOpposite()) { // skip the way we are facing
          // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }
      //don't project behind, just in front
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

        lightNodes.clear();

        for (BlockPos entry : after) {
          if (!before.contains(entry)) {
            worldObj.setBlockState(entry, EnderIO.blockLightNode.getDefaultState(), 3);
            TileEntity te = worldObj.getTileEntity(entry);
            if (te instanceof TileLightNode) {
              ((TileLightNode) te).setParentPos(getPos());
              lightNodes.add(entry);
            }
          } else {
            lightNodes.add(entry);
          }
        }
        for (BlockPos entry : before) {
          if (!after.contains(entry)) {
            TileEntity te = worldObj.getTileEntity(entry);
            if ((te instanceof TileLightNode) && (((TileLightNode) te).parent == null || ((TileLightNode) te).parent.equals(getPos()))) {
              worldObj.setBlockToAir(entry);
            }
          }
        }

      } else {
        init = false;
      }

    } finally {
      updatingLightNodes = false;
    }
  }

  private void addDiaganals(Vector3d[] diags, Vector3d trans, Set<BlockPos> result) {
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

  private void addNodeInDirection(Vector3d offset, Set<BlockPos> result) {
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
    BlockPos bp = new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ()  + (int) offset.z);
    return worldObj.getBlockState(bp).getBlock() == EnderIO.blockLightNode && worldObj.getTileEntity(bp) instanceof TileLightNode;
  }

  private void clearLightNodes() {
    if(lightNodes != null) {
      for (BlockPos ln : lightNodes) {
        if (worldObj.getBlockState(ln).getBlock() == EnderIO.blockLightNode) {
          worldObj.setBlockToAir(ln);
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, Set<BlockPos> result) {
    int x = getPos().getX() + (int) offset.x;
    int y = getPos().getY()+ (int) offset.y;
    int z = getPos().getZ() + (int) offset.z;

    if(isLightNode(offset)) {
      TileLightNode te = (TileLightNode) worldObj.getTileEntity(new BlockPos(x, y, z));
      if (te.parent != null && !getPos().equals(te.parent)) {
        // its somebody else's so leave it alone
        return;
      }
    }
    result.add(new BlockPos(x, y, z));
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
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if(!requiresPower) {
      return 0;
    }
    return LEGACY_ENERGY_INTAKE.get(DefaultCapacitorData.BASIC_CAPACITOR);
  }

  @Override
  public int getEnergyStored(EnumFacing from) {
    if(!requiresPower) {
      return 0;
    }
    return energyStoredRF;
  }

  @Override
  public int getMaxEnergyStored(EnumFacing from) {
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
