package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.power.PowerHandler;
import buildcraft.api.power.PowerHandler.PowerReceiver;
import buildcraft.api.power.PowerHandler.Type;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.BasicCapacitor;
import crazypants.enderio.power.Capacitors;
import crazypants.enderio.power.IInternalPowerReceptor;
import crazypants.enderio.power.PowerHandlerUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
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

  private List<TileLightNode> lightNodes;

  private int[] lightNodeCoords;

  private boolean updatingLightNodes = false;

  public TileElectricLight() {
    powerHandler = PowerHandlerUtil.createHandler(new BasicCapacitor(1, 6), this, Type.MACHINE);
  }

  public void onNeighborBlockChange(int blockID) {
    //if (!updatingLightNodes) {
    //  updateLightNodes();
    //}
    init = true;
  }

  public void nodeNeighbourChanged(TileLightNode tileLightNode) {
    //if (!updatingLightNodes) {
    //  updateLightNodes();
    //}
    init = true;
  }

  public void nodeRemoved(TileLightNode tileLightNode) {
    if (!updatingLightNodes) {
      //updateLightNodes();
      init = true;
    }
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
    if (worldObj.isRemote) {
      return;
    }

    boolean hasRedstone = hasRedstoneSignal();
    powerHandler.update();
    if (hasRedstone) {
      powerHandler.setEnergy(Math.max(0, powerHandler.getEnergyStored() - MJ_USE_PER_TICK));
    }

    boolean isActivated = hasPower() && hasRedstone;
    if (init) {
      lightNodes = new ArrayList<TileLightNode>();
      updateLightNodes();
    }

    if (isActivated && !isOn() || !isActivated && isOn() || init) {
      worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isActivated ? 1 : 0, 2);
      for (TileLightNode ln : lightNodes) {
        if (ln != null) {
          worldObj.setBlockMetadataWithNotify(ln.xCoord, ln.yCoord, ln.zCoord, isActivated ? 1 : 0, 2);
        }
      }
      init = false;
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
    try {
      if (lightNodeCoords != null) {
        clearLightNodes();

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

      } else { // updating existing so kill all out current ones

        clearLightNodes();

      }

      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        if (dir != face && dir != face.getOpposite()) { // don't project behind us
          Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
          addNodeInDirection(new Vector3d(offset));
          addNodeInDirection(offset.add(ForgeDirectionOffsets.forDirCopy(face.getOpposite())));
        }
      }
      
      addNodeInDirection(ForgeDirectionOffsets.forDirCopy(face.getOpposite()));
      
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
      addDiaganals(diags, new Vector3d());      
      addDiaganals(diags,ForgeDirectionOffsets.forDirCopy(face.getOpposite()));
      

    } finally {
      updatingLightNodes = false;
    }
  }

  private void addDiaganals(Vector3d[] diags, Vector3d trans) {
    Vector3d offset = new Vector3d();      
    offset.set(diags[0]);
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans));
    
    offset.set(diags[0]);
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans));
    
    offset.set(new Vector3d(diags[0]).negate());
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans));
    
    offset.set(new Vector3d(diags[0]).negate());
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans));
  }

  private void addNodeInDirection(Vector3d offset) {    
    
    boolean isAir = isAir(offset);
    boolean isTransp = isTranparent(offset);
    if (isAir || isTransp) {
      offset.scale(2);
      if (isAir(offset)) {
        addLightNode(offset);
      } else if (isAir) {
        offset.scale(0.5);
        addLightNode(offset);
      }
    }
  }


  private void clearLightNodes() {
    if (lightNodes != null) {
      for (TileLightNode ln : lightNodes) {        
        worldObj.setBlockToAir(ln.xCoord, ln.yCoord, ln.zCoord);
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset) {
    worldObj.setBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z, ModObject.blockLightNode.actualId);
    TileLightNode ln = (TileLightNode)worldObj.getBlockTileEntity(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z);
    ln.parentX = xCoord;
    ln.parentY = yCoord;
    ln.parentZ = zCoord;

    boolean isActivated = hasPower() && hasRedstoneSignal();
    worldObj.setBlockMetadataWithNotify(ln.xCoord, ln.yCoord, ln.zCoord, isActivated ? 1 : 0, 2);

    lightNodes.add(ln);
  }

  private boolean isTranparent(Vector3d offset) {
    return worldObj.getBlockLightOpacity(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == 0;
  }

  private boolean isAir(Vector3d offset) {
    return worldObj.isAirBlock(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) || 
        worldObj.getBlockId(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == ModObject.blockLightNode.actualId;
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

}
