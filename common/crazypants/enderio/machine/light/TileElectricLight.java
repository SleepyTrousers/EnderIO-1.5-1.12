package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class TileElectricLight extends TileEntity implements IInternalPowerReceptor {

  private ForgeDirection face = ForgeDirection.DOWN;

  public static final float MJ_USE_PER_TICK = 0.05f;

  protected PowerHandler powerHandler;

  private boolean init = true;

  private List<TileLightNode> lightNodes;

  private int[] lightNodeCoords;

  private boolean updatingLightNodes = false;

  private boolean lastActive = false;

  public TileElectricLight() {
    powerHandler = PowerHandlerUtil.createHandler(Capacitors.BASIC_CAPACITOR.capacitor, this, Type.MACHINE);
  }

  public void onNeighborBlockChange(int blockID) {
    init = true;
  }

  public void nodeNeighbourChanged(TileLightNode tileLightNode) {
    init = true;
  }

  public void nodeRemoved(TileLightNode tileLightNode) {
    if (!updatingLightNodes) {
      init = true;
    }
  }

  public ForgeDirection getFace() {
    return face;
  }

  public void setFace(ForgeDirection face) {
    this.face = face;
  }

  @Override
  public void updateEntity() {
    if (worldObj.isRemote) {
      return;
    }

    boolean hasRedstone = hasRedstoneSignal();
    float stored = powerHandler.getEnergyStored();
    powerHandler.update();
    powerHandler.setEnergy(stored);

    if (hasRedstone) {
      powerHandler.setEnergy(Math.max(0, powerHandler.getEnergyStored() - MJ_USE_PER_TICK));
    }

    boolean isActivated = hasPower() && hasRedstone;
    if (init) {
      updateLightNodes();
    }

    if (isActivated != lastActive || init) {
      worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActivated ? 1 : 0, 2);
      for (TileLightNode ln : lightNodes) {
        if (ln != null) {
          worldObj.setBlockMetadataWithNotify(ln.xCoord, ln.yCoord, ln.zCoord, isActivated ? 1 : 0, 2);
          worldObj.markBlockForUpdate(ln.xCoord, ln.yCoord, ln.zCoord);
          worldObj.updateLightByType(EnumSkyBlock.Block, ln.xCoord, ln.yCoord, ln.zCoord);
        }
      }
      worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
      worldObj.updateLightByType(EnumSkyBlock.Block, xCoord, yCoord, zCoord);
      init = false;
      lastActive = isActivated;
    }
  }

  public void onBlockRemoved() {
    updatingLightNodes = true;
    try {
      clearLightNodes();
    } finally {
      updatingLightNodes = false;
    }
  }

  private void updateLightNodes() {
    updatingLightNodes = true;
    List<NodeEntry> before = new ArrayList<NodeEntry>(17);
    if (lightNodes != null) {
      for (TileLightNode node : lightNodes) {
        before.add(new NodeEntry(node));
      }
    }
    List<NodeEntry> after = new ArrayList<NodeEntry>(17);
    try {
      if (lightNodeCoords != null) {

        // just loaded
        lightNodes = new ArrayList<TileLightNode>();
        for (int i = 0; i < lightNodeCoords.length; i += 3) {
          TileEntity te = worldObj.getBlockTileEntity(lightNodeCoords[i], lightNodeCoords[i + 1], lightNodeCoords[i + 2]);
          if (te instanceof TileLightNode) {
            lightNodes.add((TileLightNode) te);
          }
        }
        lightNodeCoords = null;

      } else if (lightNodes == null) { // just created

        lightNodes = new ArrayList<TileLightNode>();

      }

      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if (dir != face && dir != face.getOpposite()) { // don't project behind
                                                        // us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset), after);
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())), after);
        }
      }

      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      Vector3d[] diags = new Vector3d[2];
      if (face.offsetX != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else if (face.offsetY != 0) {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.SOUTH);
      } else {
        diags[0] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.UP);
        diags[1] = ForgeDirectionOffsets.forDirCopy(ForgeDirection.EAST);
      }
      addDiaganals(diags, new Vector3d(), after);
      addDiaganals(diags, ForgeDirectionOffsets.forDirCopy(face.getOpposite()), after);

      if (!areEqual(before, after)) {

        clearLightNodes();

        for (NodeEntry entry : after) {
          worldObj.setBlock(entry.coord.x, entry.coord.y, entry.coord.z, ModObject.blockLightNode.actualId);
          TileEntity te = worldObj.getBlockTileEntity(entry.coord.x, entry.coord.y, entry.coord.z);
          if (te instanceof TileLightNode) {
            TileLightNode ln = (TileLightNode) te;
            ln.parentX = xCoord;
            ln.parentY = yCoord;
            ln.parentZ = zCoord;
            ln.isDiagnal = entry.isDiagnal;
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

  private boolean areEqual(List<NodeEntry> before, List<NodeEntry> after) {
    if (before.size() != after.size()) {
      return false;
    }
    for (NodeEntry entry : before) {
      if (!after.contains(entry)) {
        return false;
      }
    }
    return true;
  }

  private void addDiaganals(Vector3d[] diags, Vector3d trans, List<NodeEntry> result) {
    Vector3d offset = new Vector3d();
    offset.set(diags[0]);
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(diags[0]);
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(new Vector3d(diags[0]).negate());
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);

    offset.set(new Vector3d(diags[0]).negate());
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true, result);
  }

  private void addNodeInDirection(Vector3d offset, List<NodeEntry> after) {
    addNodeInDirection(offset, false, after);
  }

  private void addNodeInDirection(Vector3d offset, boolean diagnal, List<NodeEntry> result) {

    boolean isAir = isAir(offset);
    boolean isTransp = isTranparent(offset);
    if (isAir || isTransp) {
      offset.scale(2);
      if (isAir(offset)) {
        addLightNode(offset, diagnal, result);
      } else if (isAir) {
        offset.scale(0.5);
        addLightNode(offset, diagnal, result);
      }
    }
  }

  private boolean isLightNode(Vector3d offset) {
    return worldObj.getBlockId(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == ModObject.blockLightNode.actualId;
  }

  private void clearLightNodes() {
    if (lightNodes != null) {
      for (TileLightNode ln : lightNodes) {
        if (worldObj.getBlockId(ln.xCoord, ln.yCoord, ln.zCoord) == ModObject.blockLightNode.actualId) {
          worldObj.setBlockToAir(ln.xCoord, ln.yCoord, ln.zCoord);
        }
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, boolean isDiag, List<NodeEntry> result) {

    int x = xCoord + (int) offset.x;
    int y = yCoord + (int) offset.y;
    int z = zCoord + (int) offset.z;

    if (isLightNode(offset)) {
      TileLightNode te = (TileLightNode) worldObj.getBlockTileEntity(x, y, z);
      if (te.parentX != xCoord || te.parentY != yCoord || te.parentZ != zCoord) {
        // its somebody else's so leave it alone
        return;
      }
    }
    result.add(new NodeEntry(new BlockCoord(x, y, z), isDiag));
  }

  private boolean isRailcraftException(int id) {
    if (id > 0 && Block.blocksList[id] != null) {
      // Pretty bad hack, by only feasable way I can think of to prevent our
      // light nodes getting placed inside railcraft tanks.
      String className = Block.blocksList[id].getClass().getName();
      if (className.equals("mods.railcraft.common.blocks.machine.BlockMachine")) {
        return true;
      }
    }
    return false;
  }

  private boolean isTranparent(Vector3d offset) {
    int id = worldObj.getBlockId(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z);
    if (isRailcraftException(id)) {
      return false;
    }
    return worldObj.getBlockLightOpacity(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return worldObj.isAirBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) || isLightNode(offset);
  }

  @Override
  public void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
    face = ForgeDirection.values()[root.getShort("face")];

    float storedEnergy = root.getFloat("storedEnergy");
    powerHandler.setEnergy(storedEnergy);
    lightNodeCoords = root.getIntArray("lightNodes");
  }

  @Override
  public void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    root.setShort("face", (short) face.ordinal());
    root.setFloat("storedEnergy", powerHandler.getEnergyStored());

    if (lightNodes != null) {
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
    return powerHandler.getEnergyStored() > MJ_USE_PER_TICK;
  }

  private boolean hasRedstoneSignal() {
    return worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
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

  // RF Power

  @Override
  public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
    return PowerHandlerUtil.recieveRedstoneFlux(from, powerHandler, maxReceive, simulate);
  }

  @Override
  public int extractEnergy(ForgeDirection from, int maxExtract, boolean simulate) {
    return 0;
  }

  @Override
  public boolean canInterface(ForgeDirection from) {
    return true;
  }

  @Override
  public int getEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getEnergyStored() * 10);
  }

  @Override
  public int getMaxEnergyStored(ForgeDirection from) {
    return (int) (powerHandler.getMaxEnergyStored() * 10);
  }

  static class NodeEntry {
    final BlockCoord coord;
    final boolean isDiagnal;

    NodeEntry(BlockCoord coord, boolean isDiagnal) {
      this.coord = coord;
      this.isDiagnal = isDiagnal;
    }

    NodeEntry(TileLightNode node) {
      coord = new BlockCoord(node);
      isDiagnal = node.isDiagnal;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((coord == null) ? 0 : coord.hashCode());
      result = prime * result + (isDiagnal ? 1231 : 1237);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      NodeEntry other = (NodeEntry) obj;
      if (coord == null) {
        if (other.coord != null) {
          return false;
        }
      } else if (!coord.equals(other.coord)) {
        return false;
      }
      if (isDiagnal != other.isDiagnal) {
        return false;
      }
      return true;
    }

  }

}
