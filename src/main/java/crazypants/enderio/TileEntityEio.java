package crazypants.enderio;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.machine.PacketProgress;
import crazypants.enderio.machine.gui.AbstractMachineContainer;
import crazypants.enderio.network.PacketHandler;
import crazypants.util.BlockCoord;
import crazypants.util.IProgressTile;
import crazypants.util.Util;

public abstract class TileEntityEio extends TileEntity {

  private final int checkOffset = (int) (Math.random() * 20);
  private final boolean isProgressTile;

  protected int lastProgressScaled = -1;
  protected int ticksSinceLastProgressUpdate;

  public TileEntityEio() {
    isProgressTile = this instanceof IProgressTile;
  }

  @Override
  public final boolean canUpdate() {
    return shouldUpdate() || isProgressTile;
  }

  protected boolean shouldUpdate() {
    return true;
  }

  @Override
  public final void updateEntity() {
    doUpdate();
    if(isProgressTile) {
      int curScaled = getProgressScaled(16);
      if(++ticksSinceLastProgressUpdate >= getProgressUpdateFreq() || curScaled != lastProgressScaled) {
        sendTaskProgressPacket();
        lastProgressScaled = curScaled;
      }
    }
  }

  public final int getProgressScaled(int scale) {
    if(isProgressTile) {
      return Util.getProgressScaled(scale, (IProgressTile) this);
    }
    return 0;
  }

  protected void doUpdate() {

  }

  protected void sendTaskProgressPacket() {
    if(isProgressTile) {
      PacketHandler.sendToAllAround(new PacketProgress((IProgressTile) this), this);
    }
    ticksSinceLastProgressUpdate = 0;
  }

  /**
   * Controls how often progress updates. Has no effect if your TE is not
   * {@link IProgressTile}.
   */
  protected int getProgressUpdateFreq() {
    return 20;
  }

  @Override
  public final void readFromNBT(NBTTagCompound root) {
    super.readFromNBT(root);
    readCustomNBT(root);
  }

  @Override
  public final void writeToNBT(NBTTagCompound root) {
    super.writeToNBT(root);
    writeCustomNBT(root);
  }

  @Override
  public Packet getDescriptionPacket() {
    NBTTagCompound tag = new NBTTagCompound();
    writeCustomNBT(tag);
    return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    readCustomNBT(pkt.func_148857_g());
  }

  protected abstract void writeCustomNBT(NBTTagCompound root);

  protected abstract void readCustomNBT(NBTTagCompound root);

  protected void updateBlock() {
    if(worldObj != null) {
      worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
  }

  protected boolean isPoweredRedstone() {
    return worldObj.getStrongestIndirectPower(xCoord, yCoord, zCoord) > 0;
  }

  /**
   * Called directly after the TE is constructed. This is the place to call
   * non-final methods.
   */
  public void init() {
  }

  private BlockCoord cachedLocation = null;

  public BlockCoord getLocation() {
    return cachedLocation == null || !cachedLocation.equals(xCoord, yCoord, zCoord) ? (cachedLocation = new BlockCoord(this)) : cachedLocation;
  }

  /**
   * Call this with an interval (in ticks) to find out if the current tick is
   * the one you want to do some work. This is staggered so the work of
   * different TEs is stretched out over time.
   * 
   * @see #shouldDoWorkThisTick(int, int) If you need to offset work ticks
   */
  protected boolean shouldDoWorkThisTick(int interval) {
    return shouldDoWorkThisTick(interval, 0);
  }

  /**
   * Call this with an interval (in ticks) to find out if the current tick is
   * the one you want to do some work. This is staggered so the work of
   * different TEs is stretched out over time.
   * 
   * If you have different work items in your TE, use this variant to stagger
   * your work.
   */
  protected boolean shouldDoWorkThisTick(int interval, int offset) {
    return (worldObj.getTotalWorldTime() + checkOffset + offset) % interval == 0;
  }
}
