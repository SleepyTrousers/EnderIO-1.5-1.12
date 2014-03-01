package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import crazypants.enderio.TileEntityEio;

public class TileEntityPaintedBlock extends TileEntityEio {

  private static final String KEY_SOURCE_BLOCK_ID = "sourceBlock";
  private static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";
  private Block sourceBlock;
  private int sourceBlockMetadata;

  public TileEntityPaintedBlock() {
    this.sourceBlock = null;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    String sourceBlockStr = nbtRoot.getString(KEY_SOURCE_BLOCK_ID);
    sourceBlock = Block.getBlockFromName(sourceBlockStr);
    sourceBlockMetadata = nbtRoot.getInteger(KEY_SOURCE_BLOCK_META);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    if(sourceBlock != null) {
      nbtRoot.setString(KEY_SOURCE_BLOCK_ID, Block.blockRegistry.getNameForObject(sourceBlock));
    }
    nbtRoot.setInteger(KEY_SOURCE_BLOCK_META, sourceBlockMetadata);
  }

  public Block getSourceBlock() {
    return sourceBlock;
  }

  public void setSourceBlock(Block sourceBlock) {
    this.sourceBlock = sourceBlock;
  }

  public int getSourceBlockMetadata() {
    return sourceBlockMetadata;
  }

  public void setSourceBlockMetadata(int sourceBlockMetadata) {
    this.sourceBlockMetadata = sourceBlockMetadata;
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

}
