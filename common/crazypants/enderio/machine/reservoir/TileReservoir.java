package crazypants.enderio.machine.reservoir;

import static net.minecraftforge.common.ForgeDirection.EAST;
import static net.minecraftforge.common.ForgeDirection.NORTH;
import static net.minecraftforge.common.ForgeDirection.UNKNOWN;
import static net.minecraftforge.common.ForgeDirection.UP;
import static net.minecraftforge.common.ForgeDirection.WEST;
import static net.minecraftforge.fluids.FluidContainerRegistry.BUCKET_VOLUME;

import java.util.ArrayList;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import crazypants.enderio.ModObject;
import crazypants.enderio.PacketHandler;
import crazypants.render.BoundingBox;
import crazypants.util.BlockCoord;
import crazypants.vecmath.Vector3f;

public class TileReservoir extends TileEntity implements IFluidHandler {

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
  }

  static final FluidStack WATER_BUCKET = FluidRegistry.getFluidStack("water", BUCKET_VOLUME);

  BlockCoord[] multiblock = null;

  // Orientation of multibock
  ForgeDirection front = UNKNOWN;
  ForgeDirection up = UNKNOWN;
  ForgeDirection right = UNKNOWN;

  // Position within multiblock
  Pos pos = Pos.UNKNOWN;

  ReservoirTank tank = new ReservoirTank(BUCKET_VOLUME);

  ReservoirTank regenTank = null;

  boolean autoEject;

  private boolean tankDirty = false;

  private boolean neighboursDirty = false;

  private long ticksSinceFill = 0;

  private BoundingBox liquidRenderBounds = null;

  private ArrayList<TankNeighbour> tankNeighbours;

  @Override
  public void updateEntity() {
    if(worldObj == null || worldObj.isRemote) {
      return;
    }

    if(isMaster()) {
      //sanity check to prevent crash when moved using redstone in motion
      if(regenTank == null || tank == null) {
        return;
      }

      if(regenTank.isFull() && !tank.isFull()) {
        ++ticksSinceFill;
        if(ticksSinceFill >= 20) {
          ticksSinceFill = 0;
          tank.fill(WATER_BUCKET, true);
          tankDirty = true;
        }
      }
      if(autoEject && neighboursDirty) {
        doUpdateTankNeighbours();
      }
      if(autoEject && tankNeighbours != null && !tankNeighbours.isEmpty() && tank.getAmount() > 0) {
        int ejectable = tank.getAmount();
        int amountPerNeighbour = ejectable / tankNeighbours.size();
        FluidStack source = WATER_BUCKET.copy();
        int used = 0;
        for (TankNeighbour tc : tankNeighbours) {
          source.amount = amountPerNeighbour;
          used += tc.container.fill(tc.fillFromDir, source, true);
        }
        tank.drain(used, true);
        tankDirty = true;
      }
    }
    if(tankDirty) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
      tankDirty = false;
    }
  }

  @Override
  public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(isMultiblock()) {
      return 0;
    }
    return getController().doFill(from, resource, doFill);
  }

  @Override
  public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
    return getController().doDrain(from, maxDrain, doDrain);
  }

  @Override
  public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
    if(resource != null && !resource.isFluidEqual(tank.getFluid())) {
      return null;
    }
    return drain(from, resource.amount, doDrain);
  }

  @Override
  public boolean canFill(ForgeDirection from, Fluid fluid) {
    if(tank.getFluid() == null) {
      return true;
    }
    if(fluid != null && fluid.getID() == tank.getFluid().fluidID) {
      return true;
    }
    return false;
  }

  @Override
  public boolean canDrain(ForgeDirection from, Fluid fluid) {
    if(tank.getFluid() == null || fluid == null) {
      return false;
    }
    return tank.getFluid().getFluid().getID() == fluid.getID();
  }

  @Override
  public FluidTankInfo[] getTankInfo(ForgeDirection from) {
    return getController().doGetTankInfo(from);
  }

  private FluidTankInfo[] doGetTankInfo(ForgeDirection from) {
    return new FluidTankInfo[] { tank.getInfo() };
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  public void setAutoEject(boolean autoEject) {
    TileReservoir c = getController();
    if(c != null) {
      c.doSetAutoEject(autoEject);
    } else {
      doSetAutoEject(autoEject);
    }
  }

  private void doSetAutoEject(boolean newVal) {
    if(newVal && !autoEject) {
      updateTankNeighbours();
    }
    this.autoEject = newVal;
  }

  public boolean isAutoEject() {
    TileReservoir c = getController();
    if(c != null) {
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
    if(c != null) {
      c.neighboursDirty = true;
    }
  }

  private void doUpdateTankNeighbours() {
    if(tankNeighbours == null) {
      tankNeighbours = new ArrayList<TankNeighbour>();
    }
    tankNeighbours.clear();
    for (BlockCoord bc : multiblock) {
      for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
        BlockCoord check = bc.getLocation(dir);
        if(!inMultiblock(check)) {
          IFluidHandler tc = getTankContainer(check);
          if(tc != null) {
            tankNeighbours.add(new TankNeighbour(tc, dir.getOpposite()));
          }
        }
      }
    }
    neighboursDirty = false;
  }

  private boolean inMultiblock(BlockCoord check) {
    for (BlockCoord bc : multiblock) {
      if(check.equals(bc)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    front = ForgeDirection.getOrientation(nbtRoot.getShort("front"));
    up = ForgeDirection.getOrientation(nbtRoot.getShort("up"));
    right = ForgeDirection.getOrientation(nbtRoot.getShort("right"));
    pos = Pos.values()[nbtRoot.getShort("pos")];

    autoEject = nbtRoot.getBoolean("autoEject");

    FluidStack liquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("tank"));
    FluidStack regenLiquid = FluidStack.loadFluidStackFromNBT(nbtRoot.getCompoundTag("regenTank"));

    tank.setCapacity(regenLiquid == null ? BUCKET_VOLUME : BUCKET_VOLUME * 2);
    if(liquid != null) {
      tank.setFluid(liquid);
    } else {
      tank.setAmount(0);
    }

    if(regenLiquid == null) {
      regenTank = null;
    } else {
      regenTank = new ReservoirTank(regenLiquid, BUCKET_VOLUME * 2);
    }

    boolean wasMulti = isMultiblock();

    if(nbtRoot.getBoolean("isMultiblock")) {
      int[] coords = nbtRoot.getIntArray("multiblock");
      multiblock = new BlockCoord[4];
      int c = 0;
      for (int i = 0; i < 4; i++) {
        multiblock[i] = new BlockCoord(coords[c++], coords[c++], coords[c++]);
      }

      if(isMaster() && autoEject) {
        updateTankNeighbours();
      }

    } else {
      multiblock = null;
    }

    if(wasMulti != isMultiblock()) {
      liquidRenderBounds = null;
    }

  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setShort("front", (short) front.ordinal());
    nbtRoot.setShort("up", (short) up.ordinal());
    nbtRoot.setShort("right", (short) right.ordinal());
    nbtRoot.setShort("pos", (short) pos.ordinal());

    nbtRoot.setBoolean("autoEject", autoEject);

    if(tank.getFluid() != null && FluidRegistry.getFluidName(tank.getFluid().fluidID) != null) {
      nbtRoot.setTag("tank", tank.getFluid().writeToNBT(new NBTTagCompound()));
    }
    if(regenTank != null) {
      nbtRoot.setTag("regenTank", regenTank.getFluid().writeToNBT(new NBTTagCompound()));
    }

    nbtRoot.setBoolean("isMultiblock", isMultiblock());
    if(isMultiblock()) {
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

  public boolean onNeighborBlockChange(int blockId) {
    if(blockId == ModObject.blockReservoir.actualId) {

      if(!isCurrentMultiblockValid()) {
        // if its not, try and form a new one
        TileReservoir controller = getController();
        if(controller != null) {
          controller.clearCurrentMultiblock();
          controller.formMultiblock();
        } else {
          clearCurrentMultiblock();
          formMultiblock();
        }
        return true;
      }
    } else if(isMultiblock() && isAutoEject()) {
      updateTankNeighbours();
    }
    return false;
  }

  boolean isVertical() {
    return up == UP;
  }

  boolean isMaster() {
    if(multiblock != null) {
      return multiblock[0].equals(xCoord, yCoord, zCoord);
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
    if(liquidRenderBounds == null) {
      if(!isMultiblock()) {
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
    if(isMaster()) {
      result = regenTank.getFilledRatio() * 0.5f + result * 0.5f;
    }
    return result;
  }

  int doFill(ForgeDirection from, FluidStack resource, boolean doFill) {
    if(!WATER_BUCKET.isFluidEqual(resource)) {
      return 0;
    }

    int ret = 0;
    // fill buffer first
    if(resource != null && isMaster()) {
      resource = resource.copy();
      int filled = regenTank.fill(resource, doFill);
      resource.amount -= filled;
      ret += filled;
    }
    ret += tank.fill(resource, doFill);
    tankDirty = doFill;
    return ret;
  }

  protected FluidStack doDrain(ForgeDirection from, int maxDrain, boolean doDrain) {
    FluidStack ret = tank.drain(maxDrain, doDrain);
    tankDirty = doDrain;
    return ret;
  }

  private void setMultiblock(BlockCoord[] mb) {
    multiblock = mb;
    updatePosition();

    if(isMaster()) {

      regenTank = new ReservoirTank(BUCKET_VOLUME * 2);
      tank.setCapacity(BUCKET_VOLUME * 2);
      for (BlockCoord bc : multiblock) {
        TileReservoir res = getReservoir(bc);
        if(res != null) {
          FluidStack drained = res.doDrain(ForgeDirection.UNKNOWN, regenTank.getAvailableSpace(), true);
          if(drained != null) {
            regenTank.addAmount(drained.amount);
          }
          // incase regen tank is full, add to normal tank
          drained = res.doDrain(ForgeDirection.UNKNOWN, tank.getAvailableSpace(), true);
          if(drained != null) {
            tank.addAmount(drained.amount);
          }
        }
      }

      if(doIsAutoEject()) {
        updateTankNeighbours();
      }

    } else {
      regenTank = null;
      tank.setCapacity(BUCKET_VOLUME);
    }
    tankDirty = true;

    liquidRenderBounds = null;

    // Forces an update
    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, isMultiblock() ? 1 : 0, 2);
  }

  TileReservoir getController() {
    if(isMaster() || !isMultiblock()) {
      return this;
    }
    TileReservoir res = getReservoir(multiblock[0]);
    return res != null ? res : this;
  }

  private void updatePosition() {
    if(multiblock == null) {
      front = UNKNOWN;
      up = UNKNOWN;
      right = UNKNOWN;
      return;
    }

    boolean isVertical = false;
    for (BlockCoord bc : multiblock) {
      if(bc.y != yCoord) {
        isVertical = true;
        break;
      }
    }
    if(isVertical) {
      up = ForgeDirection.UP;
      boolean isWestEast = false;
      for (BlockCoord bc : multiblock) {
        if(bc.x != xCoord) {
          isWestEast = true;
          break;
        }
      }
      front = isWestEast ? NORTH : EAST;
      right = isWestEast ? WEST : NORTH;
    } else {
      front = UP;
      right = EAST;
      up = NORTH;
    }

    boolean isRight = false;
    BlockCoord myCoord = new BlockCoord(this);
    for (BlockCoord bc : multiblock) {
      if(isInDir(myCoord, right, bc)) {
        isRight = true;
      }
    }
    boolean isTop = false;
    for (BlockCoord bc : multiblock) {
      if(isInDir(myCoord, up, bc)) {
        isTop = true;
      }
    }
    if(isTop) {
      pos = isRight ? Pos.TR : Pos.TL;
    } else {
      pos = isRight ? Pos.BR : Pos.BL;
    }

  }

  private boolean isInDir(BlockCoord from, ForgeDirection inDir, BlockCoord to) {
    if(inDir.offsetX != 0) {
      return from.x - inDir.offsetX == to.x;
    } else if(inDir.offsetY != 0) {
      return from.y - inDir.offsetY == to.y;
    } else if(inDir.offsetZ != 0) {
      return from.z - inDir.offsetZ == to.z;
    }
    return false;
  }

  private void clearCurrentMultiblock() {
    if(multiblock == null) {
      return;
    }
    boolean fillTanks = false;
    if(isMaster()) {
      fillTanks = regenTank.isFull();
      regenTank = null;
    }

    for (BlockCoord bc : multiblock) {
      TileReservoir res = getReservoir(bc);
      if(res != null) {
        res.setMultiblock(null);
        if(fillTanks) {
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
    if(multiblock == null) {
      return false;
    }
    for (BlockCoord bc : multiblock) {
      TileReservoir res = getReservoir(bc);
      if(res == null || !res.isMultiblock()) {
        return false;
      }
    }
    return true;
  }

  private boolean formMultiblock() {
    for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
      if(isNonMultiReservoir(dir)) {
        ForgeDirection[] cans = candidates(dir);
        for (ForgeDirection neighbor : cans) {
          if(isNonMultiReservoir(neighbor)) {
            if(isNonMultiReservoir(dir.offsetX + neighbor.offsetX, dir.offsetY + neighbor.offsetY, dir.offsetZ + neighbor.offsetZ)) {
              BlockCoord[] mb = new BlockCoord[4];
              mb[0] = inDirection(dir);
              mb[1] = inDirection(neighbor);
              mb[2] = inDirection(dir.offsetX + neighbor.offsetX, dir.offsetY + neighbor.offsetY, dir.offsetZ + neighbor.offsetZ);
              mb[3] = new BlockCoord(xCoord, yCoord, zCoord);
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
    return new BlockCoord(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ);
  }

  private BlockCoord inDirection(ForgeDirection dir) {
    return inDirection(dir.offsetX, dir.offsetY, dir.offsetZ);
  }

  private IFluidHandler getTankContainer(BlockCoord bc) {
    return getTankContainer(bc.x, bc.y, bc.z);
  }

  private IFluidHandler getTankContainer(int x, int y, int z) {
    if(worldObj == null) {
      return null;
    }
    TileEntity te = worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof IFluidHandler) {
      return (IFluidHandler) te;
    }
    return null;
  }

  private TileReservoir getReservoir(BlockCoord bc) {
    return getReservoir(bc.x, bc.y, bc.z);
  }

  private TileReservoir getReservoir(int x, int y, int z) {
    TileEntity te = worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof TileReservoir) {
      return (TileReservoir) te;
    }
    return null;
  }

  private boolean isNonMultiReservoir(int offsetX, int offsetY, int offsetZ) {
    TileReservoir res = getReservoir(xCoord + offsetX, yCoord + offsetY, zCoord + offsetZ);
    if(res == null) {
      return false;
    }
    return !res.isMultiblock();
  }

  private boolean isNonMultiReservoir(ForgeDirection dir) {
    return isNonMultiReservoir(dir.offsetX, dir.offsetY, dir.offsetZ);
  }

  private ForgeDirection[] candidates(ForgeDirection matchDir) {
    ForgeDirection[] res = new ForgeDirection[4];
    res[0] = matchDir.getRotation(matchDir.offsetY == 0 ? ForgeDirection.UP : ForgeDirection.NORTH);
    res[1] = res[0].getOpposite();
    res[2] = matchDir.getRotation(matchDir.offsetX == 0 ? ForgeDirection.EAST : ForgeDirection.NORTH);
    res[3] = res[2].getOpposite();
    return res;
  }

  public Vector3f getOffsetFromController() {
    if(!isMultiblock()) {
      return new Vector3f();
    }
    BlockCoord masterBC = multiblock[0];
    BlockCoord myBC = new BlockCoord(xCoord, yCoord, zCoord);
    return new Vector3f(masterBC.x - myBC.x, masterBC.y - myBC.y, masterBC.z - myBC.z);
  }

  long lastRenderTick;
  float lastRenderPartialTick;

  public boolean haveRendered(long renderTick, float renderPartialTick) {
    TileReservoir c = getController();
    if(c.lastRenderTick == renderTick && renderPartialTick == c.lastRenderPartialTick) {
      return true;
    }

    c.lastRenderTick = renderTick;
    c.lastRenderPartialTick = renderPartialTick;

    return false;
  }

}
