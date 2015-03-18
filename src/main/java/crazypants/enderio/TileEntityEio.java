package crazypants.enderio;

import crazypants.util.BlockCoord;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * Created by CrazyPants on 27/02/14.
 */
public abstract class TileEntityEio extends TileEntity {
  
  private final int checkOffset = (int) (Math.random() * 20);

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

  /*
   * init() is called directly after the TE is constructed. This is the place to call
   * non-final methods.
   */
  public void init() {}

  public BlockCoord getLocation() {
    return new BlockCoord(this);
  }
  /*
   * Call this with an interval (in ticks) to find out if the current tick is the one you want
   * to do some work. This is staggered so the work of different TEs is stretched out over time.
   * 
   * If you have different work items in your TE, use the variant with the offset parameter to
   * stagger your work.
   */
  protected boolean shouldDoWorkThisTick(int interval) {
    return (worldObj.getTotalWorldTime() + checkOffset) % interval == 0;
  }
  protected boolean shouldDoWorkThisTick(int interval, int offset) {
    return (worldObj.getTotalWorldTime() + checkOffset + offset) % interval == 0;
  }
}
