package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import crazypants.enderio.PacketHandler;

public class TileEntityCustomBlock extends TileEntity {

  private static final String KEY_SOURCE_BLOCK_ID = "sourceBlockId";
  private static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";
  private int sourceBlockId;
  private int sourceBlockMetadata;

  public TileEntityCustomBlock() {
    this.sourceBlockId = -1;
  }

  @Override
  public void readFromNBT(NBTTagCompound nbtRoot) {
    super.readFromNBT(nbtRoot);
    sourceBlockId = nbtRoot.getInteger(KEY_SOURCE_BLOCK_ID);
    sourceBlockMetadata = nbtRoot.getInteger(KEY_SOURCE_BLOCK_META);
  }

  @Override
  public void writeToNBT(NBTTagCompound nbtRoot) {
    super.writeToNBT(nbtRoot);
    nbtRoot.setInteger(KEY_SOURCE_BLOCK_ID, sourceBlockId);
    nbtRoot.setInteger(KEY_SOURCE_BLOCK_META, sourceBlockMetadata);
  }

  public int getSourceBlockId() {
    return sourceBlockId;
  }

  public void setSourceBlockId(int sourceBlockId) {
    this.sourceBlockId = sourceBlockId;
  }

  public int getSourceBlockMetadata() {
    return sourceBlockMetadata;
  }

  public void setSourceBlockMetadata(int sourceBlockMetadata) {
    this.sourceBlockMetadata = sourceBlockMetadata;
  }

  public Block getSourceBlock() {
    if(sourceBlockId <= 0) {
      return null;
    }
    return Block.blocksList[sourceBlockId];
  }

  @Override
  public Packet getDescriptionPacket() {
    return PacketHandler.getPacket(this);
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

}
