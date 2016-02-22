package crazypants.enderio.machine.reservoir;

import java.util.ArrayList;

import com.enderio.core.api.common.util.ITankAccess;
import com.enderio.core.client.render.BoundingBox;
import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.Util;
import com.enderio.core.common.vecmath.Vector3f;

import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import crazypants.enderio.EnderIO;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.tool.SmartTank;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class TileReservoir extends TileEntityEio implements IFluidHandler, ITankAccess {

  enum Pos {
    TL(true, false),
    TR(true, true),
    BL(false, false),
    BR(false, true),
    UNKNOWN(false, false);

    boolean isTop;
    boolean isRight;

    private Pos(boolean isTop, boolean isRight) {
      this.isTop = isTop;
      this.isRight = isRight;
    }

    // These are necessary to get around texture mirroring, don't ask me
    public boolean isRight(EnumFacing side) {
      if (side == EnumFacing.EAST || side == EnumFacing.NORTH) {
        return !isRight;
      }
      return isRight;
    }

    public boolean isTop(EnumFacing side) {
      if (side == EnumFacing.EAST) {
        return !isTop;
      }
      return isTop;
    }
  }

  static final FluidStack WATER_BUCKET = FluidRegistry.getFluidStack("water", BUCKET_VOLUME);

  BlockCoord[] multiblock = null;

  // Orientation of multibock
  EnumFacing front = null;
  EnumFacing up = null;
  EnumFacing right = null;

  // Position within multiblock
  Pos pos = null;

  SmartTank tank = new SmartTank(FluidRegistry.WATER, BUCKET_VOLUME);

  SmartTank regenTank = null;

  boolean autoEject;

  private boolean tankDirty = false;

  private boolean neighboursDirty = false;

  private long ticksSinceFill = 0;

  private BoundingBox liquidRenderBounds = null;

  private ArrayList<TankNeighbour> tankNeighbours;

  @Override
  public void doUpdate() {
    if (worldObj.isRemote) {
      return;
    }

    if (isMaster()) {
      // sanity check to prevent crash when moved using redstone in motion
      if (regenTank == null || tank == null) {
        return;
      }

      if (regenTank.isFull() && !tank.isFull()) {
        ++ticksSinceFill;
        if (ticksSinceFill >= 20) {
          ticksSinceFill = 0;
          tank.fill(WATER_BUCKET, true);
          tankDirty = true;
        }
      }
      if (autoEject && neighboursDirty) {
        doUpdateTankNeighbours();
      }
      if (autoEject && tankNeighbours != null && !tankNeighbours.isEmpty() && tank.getFluidAmount() > 0) {
        int ejectable = tank.getFluidAmount();
        int amountPerNeighbour = ejectable / tankNeighbours.size();
        FluidStack source = WATER_BUCKET.copy();
        int used = 0;
        for (TankNeighbour tc : tankNeighbours) {
          source.amount = amountPerNeighbour;
          used += tc.container.fill(tc.fillFromDir, source, true);
        }
        if (used > 0) {
          tank.drain(used, true);
          tankDirty = true;
        }
      }
    }
    if (tankDirty && shouldDoWorkThisTick(2)) {
      worldObj.markBlockForUpdate(getPos());
      tankDirty = false;
    }
  }

  @Override
  public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
    if (isMultiblock()) {
      return 0;
    }
    return getController().doFill(from, resource, doFill);
  }

  @Override
  public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
    return getController().doDrain(from, maxDrain, doDrain);
  }

  @Override
  public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
    if (resource == null || !resource.isFluidEqual(tank.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public boolean canFill(EnumFacing from, Fluid fluid) {
    if (tank.getFluid() == null) {
      return true;
    }
    if (fluid != null && fluid.getID() == tank.getFluid().getFluid().getID()) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canDrain(EnumFacing from, Fluid fluid) {
    if (tank.getFluid() == null || fluid == null) {
      return false;
    }
    return tank.getFluid().getFluid().getID() == fluid.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(EnumFacing from) {
    return getController().doGetTankInfo(from);
  }

  private FluidTankInfo[] doGetTankInfo(EnumFacing from) {
    return new FluidTankInfo[] { tank.getInfo() };
  }

  public void setAutoEject(boolean autoEject) {
    TileReservoir c = getController();
    if (c != null) {
      c.doSetAutoEject(autoEject);
    } else {
      doSetAutoEject(autoEject);
    }
  }

  private void doSetAutoEject(boolean newVal) {
    if (newVal && !autoEject) {
      updateTankNeighbours();
    }
    this.autoEject = newVal;
  }

  public boolean isAutoEject() {
    TileReservoir c = getController();
    if (c != null) {
      return c.doIsAutoEject();
    } else {
      return doIsAutoEject();
    }
  }

  private boolean doIsAutoEject() {
    return autoEject;
  }

  private void updateTankNeighbours() {
    TileReservoir c = getController();
    if (c != null) {
      c.neighboursDirty = true;
    }
  }

  private void doUpdateTankNeighbours() {
    if (tankNeighbours == null) {
      tankNeighbours = new ArrayList<TankNeighbour>();
    }
    tankNeighbours.clear();
    for (BlockCoord bc : multiblock) {
      for (EnumFacing dir : EnumFacing.VALUES) {
        BlockCoord check = bc.getLocation(dir);
        if (!inMultiblock(check)) {
          IFluidHandler tc = getTankContainer(check);
          if (tc != null) {
            tankNeighbours.add(new TankNeighbour(tc, dir.getOpposite()));
          }
        }
      }
    }
    neighboursDirty = false;
  }

  private boolean inMultiblock(BlockCoord check) {
    for (BlockCoord bc : multiblock) {
      if (check.equals(bc)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {

    front = Util.readFacingFromNBT(nbtRoot, "front");
    up = Util.readFacingFromNBT(nbtRoot, "up");
    right = Util.readFacingFromNBT(nbtRoot, "right");        
    pos = Pos.values()[nbtRoot.getShort("pos")];

    autoEject = nbtRoot.getBoolean("autoEject");

    FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
    FluidStack regenLiquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("regenTank"));

    tank.setCapacity(regenLiquid == null ? BUCKET_VOLUME : BUCKET_VOLUME * 2);
    if (liquid != null) {
      tank.setFluid(liquid);
    } else {
      tank.setFluidAmount(0);
    }

    if (regenLiquid == null) {
      regenTank = null;
    } else {
      regenTank = new SmartTank(FluidRegistry.WATER, BUCKET_VOLUME * 2);
      regenTank.setFluidAmount(regenLiquid.amount);
    }

    boolean wasMulti = isMultiblock();

    if (nbtRoot.getBoolean("isMultiblock")) {
      int[] coords = nbtRoot.getIntArray("multiblock");
      multiblock = new BlockCoord[4];
      int c = 0;
      for (int i = 0; i < 4; i++) {
        multiblock[i] = new BlockCoord(coords[c++], coords[c++], coords[c++]);
      }

      if (isMaster() && autoEject) {
        updateTankNeighbours();
      }

    } else {
      multiblock = null;
    }

    if (wasMulti != isMultiblock()) {
      liquidRenderBounds = null;
    }

  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {

    Util.writeFacingToNBT(nbtRoot, "front", front);
    Util.writeFacingToNBT(nbtRoot, "up", up);
    Util.writeFacingToNBT(nbtRoot, "right", right);
    
    if (pos != null) {
      nbtRoot.setShort("pos", (short) pos.ordinal());
    }

    nbtRoot.setBoolean("autoEject", autoEject);

    if (tank.getFluid() != null && FluidRegistry.getFluidName(tank.getFluid()) != null) {
      nbtRoot.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
    }
    if (regenTank != null) {
      nbtRoot.setTag("regenTank", regenTank.getFluid().writeToNBT(new NBTTagCompound()));
    }

    nbtRoot.setBoolean("isMultiblock", isMultiblock());
    if (isMultiblock()) {
      int[] vals = new int[12];
      int i = 0;
      for (BlockCoord bc : multiblock) {
        vals[i++] = bc.x;
        vals[i++] = bc.y;
        vals[i++] = bc.z;
      }
      nbtRoot.setIntArray("multiblock", vals);
    }
  }

  public boolean onBlockAdded() {
    boolean res = formMultiblock();
    return res;
  }

  public boolean onNeighborBlockChange(Block blockId) {
    if (blockId == EnderIO.blockReservoir) {

      if (!isCurrentMultiblockValid()) {
        // if its not, try and form a new one
        TileReservoir controller = getController();
        if (controller != null) {
          controller.clearCurrentMultiblock();
          controller.formMultiblock();
        } else {
          clearCurrentMultiblock();
          formMultiblock();
        }
        return true;
      }
    } else if (isMultiblock() && isAutoEject()) {
      updateTankNeighbours();
    }
    return false;
  }

  boolean isVertical() {
    return up == EnumFacing.UP;
  }

  boolean isMaster() {
    if (multiblock != null) {
      return multiblock[0].equals(new BlockCoord(getPos()));
    }
    return false;
  }

  float getFilledRatio() {
    return getController().doGetFilledRatio();
  }

  public boolean isMultiblock() {
    return multiblock != null;
  }

  BoundingBox getLiquidRenderBounds() {
    if (liquidRenderBounds == null) {
      if (!isMultiblock()) {
        return BoundingBox.UNIT_CUBE;
      }
      BoundingBox bounds = new BoundingBox(multiblock[0]);
      for (int i = 1; i < multiblock.length; i++) {
        bounds = bounds.expandBy(new BoundingBox(multiblock[i]));
      }
      liquidRenderBounds = bounds.translate(-multiblock[0].x, -multiblock[0].y, -multiblock[0].z);
    }
    return liquidRenderBounds;
  }

  protected float doGetFilledRatio() {
    float result = tank.getFilledRatio();
    if (isMaster() && regenTank != null) {
      result = regenTank.getFilledRatio() * 0.5f + result * 0.5f;
    }
    return result;
  }

  int doFill(EnumFacing from, FluidStack resource, boolean doFill) {
    if (!WATER_BUCKET.isFluidEqual(resource)) {
      return 0;
    }

    int ret = 0;
    // fill buffer first
    if (resource != null && isMaster()) {
      resource = resource.copy();
      int filled = regenTank.fill(resource, doFill);
      resource.amount -= filled;
      ret += filled;
    }
    ret += tank.fill(resource, doFill);
    tankDirty = doFill;
    return ret;
  }

  protected FluidStack doDrain(EnumFacing from, int maxDrain, boolean doDrain) {
    FluidStack ret = tank.drain(maxDrain, doDrain);
    tankDirty = doDrain;
    return ret;
  }

  private void setMultiblock(BlockCoord[] mb) {
    multiblock = mb;
    updatePosition();

    if (isMaster()) {

      regenTank = new SmartTank(FluidRegistry.WATER, BUCKET_VOLUME * 2);
      tank.setCapacity(BUCKET_VOLUME * 2);
      for (BlockCoord bc : multiblock) {
        TileReservoir res = getReservoir(bc);
        if (res != null) {
          FluidStack drained = res.doDrain(null, regenTank.getAvailableSpace(), true);
          if (drained != null) {
            regenTank.addFluidAmount(drained.amount);
          }
          // incase regen tank is full, add to normal tank
          drained = res.doDrain(null, tank.getAvailableSpace(), true);
          if (drained != null) {
            tank.addFluidAmount(drained.amount);
          }
        }
      }

      if (doIsAutoEject()) {
        updateTankNeighbours();
      }

    } else {
      regenTank = null;
      tank.setCapacity(BUCKET_VOLUME);
    }
    tankDirty = true;

    liquidRenderBounds = null;

    // Forces an update
    // TODO: 1.8
    // worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord,
    // isMultiblock() ? 1 : 0, 2);
  }

  TileReservoir getController() {
    if (isMaster() || !isMultiblock()) {
      return this;
    }
    TileReservoir res = getReservoir(multiblock[0]);
    return res != null ? res : this;
  }

  private void updatePosition() {
    if (multiblock == null) {
      front = null;
      up = null;
      right = null;
      return;
    }

    BlockPos p = getPos();
    boolean isVertical = false;
    for (BlockCoord bc : multiblock) {
      if (bc.y != p.getY()) {
        isVertical = true;
        break;
      }
    }
    if (isVertical) {
      up = EnumFacing.UP;
      boolean isWestEast = false;
      for (BlockCoord bc : multiblock) {
        if (bc.x != p.getX()) {
          isWestEast = true;
          break;
        }
      }
      front = isWestEast ? EnumFacing.NORTH : EnumFacing.EAST;
      right = isWestEast ? EnumFacing.WEST : EnumFacing.NORTH;
    } else {
      front = EnumFacing.UP;
      right = EnumFacing.EAST;
      up = EnumFacing.NORTH;
    }

    boolean isRight = false;
    BlockCoord myCoord = new BlockCoord(this);
    for (BlockCoord bc : multiblock) {
      if (isInDir(myCoord, right, bc)) {
        isRight = true;
      }
    }
    boolean isTop = false;
    for (BlockCoord bc : multiblock) {
      if (isInDir(myCoord, up, bc)) {
        isTop = true;
      }
    }
    if (isTop) {
      pos = isRight ? Pos.TR : Pos.TL;
    } else {
      pos = isRight ? Pos.BR : Pos.BL;
    }

  }

  private boolean isInDir(BlockCoord from, EnumFacing inDir, BlockCoord to) {
    if (inDir.getFrontOffsetX() != 0) {
      return from.x - inDir.getFrontOffsetX() == to.x;
    } else if (inDir.getFrontOffsetY() != 0) {
      return from.y - inDir.getFrontOffsetY() == to.y;
    } else if (inDir.getFrontOffsetZ() != 0) {
      return from.z - inDir.getFrontOffsetZ() == to.z;
    }
    return false;
  }

  private void clearCurrentMultiblock() {
    if (multiblock == null) {
      return;
    }
    boolean fillTanks = false;
    if (isMaster()) {
      fillTanks = regenTank.isFull();
      regenTank = null;
    }

    for (BlockCoord bc : multiblock) {
      TileReservoir res = getReservoir(bc);
      if (res != null) {
        res.setMultiblock(null);
        if (fillTanks) {
          res.tank.fill(WATER_BUCKET, true);
        } else {
          res.tank.drain(BUCKET_VOLUME, true);
        }
      }
    }
    multiblock = null;
    tankDirty = true;
  }

  private boolean isCurrentMultiblockValid() {
    if (multiblock == null) {
      return false;
    }
    for (BlockCoord bc : multiblock) {
      TileReservoir res = getReservoir(bc);
      if (res == null || !res.isMultiblock()) {
        return false;
      }
    }
    return true;
  }

  private boolean formMultiblock() {
    for (EnumFacing dir : EnumFacing.VALUES) {
      if (isNonMultiReservoir(dir)) {
        EnumFacing[] cans = candidates(dir);
        for (EnumFacing neighbor : cans) {
          if (isNonMultiReservoir(neighbor)) {
            if (isNonMultiReservoir(dir.getFrontOffsetX() + neighbor.getFrontOffsetX(), dir.getFrontOffsetY() + neighbor.getFrontOffsetY(),
                dir.getFrontOffsetZ() + neighbor.getFrontOffsetZ())) {
              BlockCoord[] mb = new BlockCoord[4];
              mb[0] = inDirection(dir);
              mb[1] = inDirection(neighbor);
              mb[2] = inDirection(dir.getFrontOffsetX() + neighbor.getFrontOffsetX(), dir.getFrontOffsetY() + neighbor.getFrontOffsetY(),
                  dir.getFrontOffsetZ() + neighbor.getFrontOffsetZ());
              mb[3] = new BlockCoord(getPos());
              for (BlockCoord bc : mb) {
                TileReservoir res = getReservoir(bc);
                res.setMultiblock(mb);
              }
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  private BlockCoord inDirection(int offsetX, int offsetY, int offsetZ) {
    BlockPos p = getPos();
    return new BlockCoord(p.getX() + offsetX, p.getY() + offsetY, p.getZ() + offsetZ);
  }

  private BlockCoord inDirection(EnumFacing dir) {
    return inDirection(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
  }

  private IFluidHandler getTankContainer(BlockCoord bc) {
    return getTankContainer(bc.x, bc.y, bc.z);
  }

  private IFluidHandler getTankContainer(int x, int y, int z) {
    if (worldObj == null) {
      return null;
    }
    TileEntity te = worldObj.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof IFluidHandler) {
      return (IFluidHandler) te;
    }
    return null;
  }

  private TileReservoir getReservoir(BlockCoord bc) {
    return getReservoir(bc.x, bc.y, bc.z);
  }

  private TileReservoir getReservoir(int x, int y, int z) {
    TileEntity te = worldObj.getTileEntity(new BlockPos(x, y, z));
    if (te instanceof TileReservoir) {
      return (TileReservoir) te;
    }
    return null;
  }

  private boolean isNonMultiReservoir(int offsetX, int offsetY, int offsetZ) {
    BlockPos p = getPos();
    TileReservoir res = getReservoir(p.getX() + offsetX, p.getY() + offsetY, p.getZ() + offsetZ);
    if (res == null) {
      return false;
    }
    return !res.isMultiblock();
  }

  private boolean isNonMultiReservoir(EnumFacing dir) {
    return isNonMultiReservoir(dir.getFrontOffsetX(), dir.getFrontOffsetY(), dir.getFrontOffsetZ());
  }

  private EnumFacing[] candidates(EnumFacing matchDir) {
    EnumFacing[] res = new EnumFacing[4];
    res[0] = matchDir.rotateAround(matchDir.getFrontOffsetY() == 0 ? EnumFacing.UP.getAxis() : EnumFacing.NORTH.getAxis());
    res[1] = res[0].getOpposite();
    res[2] = matchDir.rotateAround(matchDir.getFrontOffsetX() == 0 ? EnumFacing.EAST.getAxis() : EnumFacing.NORTH.getAxis());
    res[3] = res[2].getOpposite();
    return res;
  }

  public Vector3f getOffsetFromController() {
    if (!isMultiblock()) {
      return new Vector3f();
    }
    BlockCoord masterBC = multiblock[0];
    BlockCoord myBC = new BlockCoord(getPos());
    return new Vector3f(masterBC.x - myBC.x, masterBC.y - myBC.y, masterBC.z - myBC.z);
  }

  long lastRenderTick;
  float lastRenderPartialTick;

  public boolean haveRendered(long renderTick, float renderPartialTick) {
    TileReservoir c = getController();
    if (c.lastRenderTick == renderTick && renderPartialTick == c.lastRenderPartialTick) {
      return true;
    }

    c.lastRenderTick = renderTick;
    c.lastRenderPartialTick = renderPartialTick;

    return false;
  }

  @Override
  public FluidTank getInputTank(FluidStack forFluidType) {
    if (forFluidType != null && forFluidType.getFluid() == FluidRegistry.WATER) {
      TileReservoir res = getController();
      return !res.isMaster() || res.regenTank.isFull() ? res.tank : res.regenTank;
    }
    return null;
  }

  @Override
  public FluidTank[] getOutputTanks() {
    return new FluidTank[] { getController().tank };
  }

  @Override
  public void setTanksDirty() {
    if (isMaster() && regenTank != null && tank != null) {
      if (!regenTank.isFull() && tank.getFluidAmount() > 0) {
        int toMove = Math.min(tank.getFluidAmount(), regenTank.getCapacity() - regenTank.getFluidAmount());
        regenTank.setFluidAmount(regenTank.getFluidAmount() + toMove);
        tank.setFluidAmount(tank.getFluidAmount() - toMove);
      }
    }
    tankDirty = true;
  }

}
