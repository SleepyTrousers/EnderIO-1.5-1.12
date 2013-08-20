package crazypants.enderio.machine.light;

import java.util.ArrayList;
import java.util.List;

import buildcraft.api.power.*;

import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.power.*;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileElectricLight extends TileEntity implements IInternalPowerReceptor {

  private ForgeDirection face = ForgeDirection.DOWN;

  public static final float MJ_USE_PER_TICK = 0.05f;

  protected EnderPowerProvider powerHandler;

  private boolean init = true;

  private List<TileLightNode> lightNodes;

  private int[] lightNodeCoords;

  private boolean updatingLightNodes = false;
  
  private boolean lastActive = false;

  public TileElectricLight() {
    powerHandler = PowerHandlerUtil.createHandler(Capacitors.BASIC_CAPACITOR.capacitor);
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
    if (hasRedstone) {
      powerHandler.setEnergy(Math.max(0, powerHandler.getEnergyStored() - MJ_USE_PER_TICK));
    }

    boolean isActivated = hasPower() && hasRedstone;
    if (init) {
      lightNodes = new ArrayList<TileLightNode>();
      updateLightNodes();
    }

    if (isActivated && !lastActive || init) {      
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
    addNodeInDirection(offset.add(trans), true);
    
    offset.set(diags[0]);
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true);
    
    offset.set(new Vector3d(diags[0]).negate());
    offset.add(diags[1]);
    addNodeInDirection(offset.add(trans), true);
    
    offset.set(new Vector3d(diags[0]).negate());
    offset.sub(diags[1]);
    addNodeInDirection(offset.add(trans), true);
  }

  private void addNodeInDirection(Vector3d offset) {
    addNodeInDirection(offset, false);
  }
  
  private void addNodeInDirection(Vector3d offset, boolean isDiag) {    
    
    boolean isAir = isAir(offset);
    boolean isTransp = isTranparent(offset);
    if (isAir || isTransp) {
      offset.scale(2);
      if (isAir(offset)) {        
        addLightNode(offset, isDiag);        
      } else if (isAir) {
        offset.scale(0.5);        
        addLightNode(offset, isDiag);        
      }
    }
  }
  
  private boolean isLightNode(Vector3d offset) {        
    return worldObj.getBlockId(xCoord + (int) offset.x, yCoord + (int) offset.y, zCoord + (int) offset.z) == ModObject.blockLightNode.actualId;
  }


  private void clearLightNodes() {
    if (lightNodes != null) {
      for (TileLightNode ln : lightNodes) {        
        worldObj.setBlockToAir(ln.xCoord, ln.yCoord, ln.zCoord);
      }
      lightNodes.clear();
    }
  }

  private void addLightNode(Vector3d offset, boolean isDiag) {
    
    int x = xCoord + (int) offset.x;
    int y = yCoord + (int) offset.y;
    int z = zCoord + (int) offset.z;
    
    if(isLightNode(offset)) {
      TileLightNode te = (TileLightNode)worldObj.getBlockTileEntity(x,y,z);
      if(te.parentX != xCoord || te.parentY != yCoord || te.parentZ != zCoord) {
        //its somebody else's so leave it alone
        return;
      }
    }
    
    worldObj.setBlock(x,y,z, ModObject.blockLightNode.actualId);
    TileLightNode ln = (TileLightNode)worldObj.getBlockTileEntity(x,y,z);
    ln.parentX = xCoord;
    ln.parentY = yCoord;
    ln.parentZ = zCoord;
    ln.isDiagnal = isDiag;

    lightNodes.add(ln);
  }

  private boolean isTranparent(Vector3d offset) {
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
  public void applyPerdition() {
  }

  @Override
  public void setPowerProvider(IPowerProvider provider) {    
  }

  @Override
  public IPowerProvider getPowerProvider() {
    return powerHandler;
  }

  @Override
  public void doWork() {
  }

  @Override
  public int powerRequest(ForgeDirection from) {    
    return (int)(powerHandler.getMaxEnergyStored() - powerHandler.getEnergyStored());
  }

  @Override
  public EnderPowerProvider getPowerHandler() {
    return powerHandler;
  }

}
