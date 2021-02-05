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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.Constants.BlockFlags;
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

    final boolean isActive = world.getBlockState(pos).getValue(BlockElectricLight.ACTIVE);
    final boolean shouldActive = (world.isBlockPowered(pos) ^ isInverted()) && (!requiresPower() || hasPower());

    if (requiresPower()) {
      if (init) {
        updateLightNodes();
      }
      if (isWireless()) {
        executeWirelessCharging();
      }
      if (shouldActive) {
        setEnergyStored(getEnergyStored() - RF_USE_PER_TICK);
      }

    }

    if (init || (isActive != shouldActive)) {
      setBlockstateAndNodes(shouldActive);
    }

    init = false;
  }

  public boolean providesPoweredLight() {
    return (world.isBlockPowered(pos) ^ isInverted()) && (requiresPower() && hasPower());
  }

  private void executeWirelessCharging() {
    if (energyStoredRF < getMaxEnergyStored() / 2) {
      if (chargedLocation == null) {
        chargedLocation = new WirelessChargedLocation(this);
      }
      energyStoredRF += chargedLocation.takeEnergy(Math.min(getMaxEnergyStored() - energyStoredRF, 10));
    }
  }

  private void setBlockstateAndNodes(boolean isActivated) {
    IBlockState bs = world.getBlockState(pos);
    IBlockState bsnew = bs.withProperty(BlockElectricLight.ACTIVE, isActivated);
    world.setBlockState(pos, bsnew, BlockFlags.SEND_TO_CLIENTS);

    if (requiresPower()) {
      for (BlockPos ln : lightNodes) {
        if (ln != null) {
          TileEntity te = world.getTileEntity(ln);
          if ((te instanceof TileLightNode)) {
            ((TileLightNode) te).calculateLight();
          }
        }
      }
    }

    world.notifyBlockUpdate(pos, bs, bsnew, Constants.BlockFlags.DEFAULT);
    world.checkLightFor(EnumSkyBlock.BLOCK, pos);
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

      // TODO: This a a mess much too big for just a handful of lightnode.
      // (1) Clean this up
      // (2) Extend the range of lightnodes to something more useful

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
              ((TileLightNode) te).addParent(getPos());
              lightNodes.add(entry);
            }
          } else {
            lightNodes.add(entry);
          }
        }
        for (BlockPos entry : before) {
          if (!after.contains(entry) && entry != null) {
            TileEntity te = world.getTileEntity(entry);
            if ((te instanceof TileLightNode)) {
              ((TileLightNode) te).removeParent(getPos());
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

  private void clearLightNodes() {
    if (lightNodes != null) {
      for (BlockPos ln : lightNodes) {
        if (ln != null) {
          TileEntity te = world.getTileEntity(ln);
          if ((te instanceof TileLightNode)) {
            ((TileLightNode) te).removeParent(getPos());
          }
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, Set<BlockPos> result) {
    int x = getPos().getX() + (int) offset.x;
    int y = getPos().getY() + (int) offset.y;
    int z = getPos().getZ() + (int) offset.z;

    result.add(new BlockPos(x, y, z));
  }

  private boolean isTransparent(Vector3d offset) {
    return world.getBlockLightOpacity(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z)) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return world.isAirBlock(new BlockPos(getPos().getX() + (int) offset.x, getPos().getY() + (int) offset.y, getPos().getZ() + (int) offset.z));
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
