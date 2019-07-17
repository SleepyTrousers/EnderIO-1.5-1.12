package crazypants.enderio.machines.machine.light;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.base.TileEntityEio;
import crazypants.enderio.base.power.PowerHandlerUtil;
import crazypants.enderio.base.power.forge.tile.ILegacyPoweredTile;
import crazypants.enderio.base.power.forge.tile.InternalRecieverTileWrapper;
import crazypants.enderio.base.power.wireless.WirelessChargedLocation;
import info.loenwind.autosave.annotations.Store;
import info.loenwind.autosave.util.NBTAction;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraftforge.energy.CapabilityEnergy;

import static crazypants.enderio.base.capacitor.CapacitorKey.LEGACY_ENERGY_INTAKE;
import static crazypants.enderio.machines.init.MachineObject.block_light_node;

public class TileElectricLight extends TileEntityEio implements ILegacyPoweredTile.Receiver {

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  private @Nonnull EnumFacing face = EnumFacing.DOWN;

  public static final int RF_USE_PER_TICK = 1;

  private boolean init = true;

  @Store(value = NBTAction.SAVE)
  private List<BlockPos> lightNodes;

  private boolean updatingLightNodes = false;

  private boolean lastActive = false;

  private WirelessChargedLocation chargedLocation;

  @Store({ NBTAction.SAVE, NBTAction.CLIENT })
  private int energyStoredRF;

  public TileElectricLight() {
    addICap(CapabilityEnergy.ENERGY, facing -> InternalRecieverTileWrapper.get(this, facing));
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
    if (!updatingLightNodes) {
      init = true;
    }
  }

  public @Nonnull EnumFacing getFace() {
    return face;
  }

  public void setFace(@Nonnull EnumFacing face) {
    this.face = face;
  }

  private transient LightType lighttype = null;

  public @Nonnull LightType getLightType() {
    return lighttype != null ? lighttype : (lighttype = world.getBlockState(getPos()).getValue(BlockElectricLight.TYPE));
  }

  public boolean requiresPower() {
    return getLightType().isPowered();
  }

  public boolean isInverted() {
    return getLightType().isInverted();
  }

  public boolean isWireless() {
    return getLightType().isWireless();
  }

  @Override
  public void doUpdate() {
    if (world.isRemote) {
      super.doUpdate(); // disable ticking on the client
      return;
    }

    boolean isActivated = init ? world.isBlockPowered(pos) ^ isInverted() : lastActive;
    if (requiresPower()) {
      if (isActivated) {
        if (!hasPower()) {
          isActivated = false;
        } else {
          setEnergyStored(getEnergyStored() - RF_USE_PER_TICK);
        }
      }

      if (init) {
        updateLightNodes();
      }
    }

    if (isActivated != lastActive || init) {

      IBlockState bs = world.getBlockState(pos);
      bs = bs.withProperty(BlockElectricLight.ACTIVE, isActivated);
      world.setBlockState(pos, bs, 2);

      if (requiresPower()) {
        for (BlockPos ln : lightNodes) {
          if (ln != null) {
            bs = world.getBlockState(ln);
            if (bs.getBlock() == block_light_node.getBlock()) {
              bs = bs.withProperty(BlockLightNode.ACTIVE, isActivated);
              world.setBlockState(ln, bs, 2);
              world.notifyBlockUpdate(ln, bs, bs, 3);
              world.checkLightFor(EnumSkyBlock.BLOCK, ln);
            }
          }
        }
      }
      world.notifyBlockUpdate(pos, bs, bs, 3);
      world.checkLightFor(EnumSkyBlock.BLOCK, pos);
      init = false;
      lastActive = isActivated;

    }

    if (isWireless()) {
      if (chargedLocation == null) {
        chargedLocation = new WirelessChargedLocation(this);
      }
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
    if (!requiresPower()) {
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
    if (lightNodes != null && !lightNodes.isEmpty()) {
      before = new HashSet<BlockPos>(lightNodes.size());
      before.addAll(lightNodes);
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
        if (dir != face && dir != face.getOpposite()) { // skip the way we are facing
          // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }
      // don't project behind, just in front
      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      Vector3d[] diags = new Vector3d[2];
      if (face.getFrontOffsetX() != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.SOUTH);
      } else if (face.getFrontOffsetY() != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.EAST);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.SOUTH);
      } else {
        diags[0] = ForgeDirectionOffsets.forDirCopy(EnumFacing.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(EnumFacing.EAST);
      }
      addDiaganals(diags, new Vector3d(), after);
      addDiaganals(diags, ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      if (!before.equals(after)) {

        lightNodes.clear();

        for (BlockPos entry : after) {
          if (!before.contains(entry) && entry != null) {
            world.setBlockState(entry, block_light_node.getBlockNN().getDefaultState(), 3);
            TileEntity te = world.getTileEntity(entry);
            if (te instanceof TileLightNode) {
              ((TileLightNode) te).setParentPos(getPos());
              lightNodes.add(entry);
            }
          } else {
            lightNodes.add(entry);
          }
        }
        for (BlockPos entry : before) {
          if (!after.contains(entry) && entry != null) {
            TileEntity te = world.getTileEntity(entry);
            if ((te instanceof TileLightNode) && (((TileLightNode) te).getParentPos().equals(getPos()))) {
              world.setBlockToAir(entry);
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
    boolean isTransp = isTransparent(offset);
    if (isAir || isTransp) {
      offset.scale(2);
      if (isAir(offset)) {
        addLightNode(offset, result);
      } else if (isAir) {
        offset.scale(0.5);
        addLightNode(offset, result);
      }
    }
  }

  private boolean isLightNode(Vector3d offset) {
    BlockPos bp = new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z);
    return world.getBlockState(bp).getBlock() == block_light_node.getBlock() && world.getTileEntity(bp) instanceof TileLightNode;
  }

  private void clearLightNodes() {
    if (lightNodes != null) {
      for (BlockPos ln : lightNodes) {
        if (ln != null && world.getBlockState(ln).getBlock() == block_light_node.getBlock()) {
          world.setBlockToAir(ln);
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, Set<BlockPos> result) {
    int x = getPos().getX() + (int) offset.x;
    int y = getPos().getY() + (int) offset.y;
    int z = getPos().getZ() + (int) offset.z;

    if (isLightNode(offset)) {
      TileLightNode te = (TileLightNode) world.getTileEntity(new BlockPos(x, y, z));
      if (te != null && !getPos().equals(te.getParentPos())) {
        // its somebody else's so leave it alone
        return;
      }
    }
    result.add(new BlockPos(x, y, z));
  }

  private boolean isTransparent(Vector3d offset) {
    return world.getBlockLightOpacity(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z)) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return world.isAirBlock(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z))
        || isLightNode(offset);
  }

  public boolean hasPower() {
    return energyStoredRF >= RF_USE_PER_TICK;
  }

  // RF Power

  @Override
  public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
    if (!requiresPower()) {
      return 0;
    }
    if (energyStoredRF == 0) {
      init = true;
    }
    return PowerHandlerUtil.recieveInternal(this, maxReceive, from, simulate);
  }

  @Override
  public boolean canConnectEnergy(@Nonnull EnumFacing from) {
    return requiresPower();
  }

  @Override
  public int getMaxEnergyRecieved(EnumFacing dir) {
    if (!requiresPower()) {
      return 0;
    }
    return LEGACY_ENERGY_INTAKE.getDefault();
  }

  @Override
  public int getEnergyStored() {
    if (!requiresPower()) {
      return 0;
    }
    return energyStoredRF;
  }

  @Override
  public int getMaxEnergyStored() {
    if (!requiresPower()) {
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
    return requiresPower();
  }

  @Override
  public @Nonnull BlockPos getLocation() {
    return pos;
  }

}
