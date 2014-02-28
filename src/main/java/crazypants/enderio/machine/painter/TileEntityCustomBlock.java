package crazypants.enderio.machine.painter;

import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.common.registry.GameData;
import crazypants.enderio.TileEntityEio;

public class TileEntityCustomBlock extends TileEntityEio {

  private static final String KEY_SOURCE_BLOCK_ID = "sourceBlockId";
  private static final String KEY_SOURCE_BLOCK_META = "sourceBlockMeta";
  private String sourceBlockId;
  private int sourceBlockMetadata;

  public TileEntityCustomBlock() {
    this.sourceBlockId = null;
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    sourceBlockId = nbtRoot.getString(KEY_SOURCE_BLOCK_ID);
    sourceBlockMetadata = nbtRoot.getInteger(KEY_SOURCE_BLOCK_META);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    if(sourceBlockId != null && sourceBlockId.trim().length() > 0) {
      nbtRoot.setString(KEY_SOURCE_BLOCK_ID, sourceBlockId);
    }
    nbtRoot.setInteger(KEY_SOURCE_BLOCK_META, sourceBlockMetadata);
  }

  public String getSourceBlockId() {
    return sourceBlockId;
  }

  public void setSourceBlockId(String sourceBlockId) {
    this.sourceBlockId = sourceBlockId;
  }

  public int getSourceBlockMetadata() {
    return sourceBlockMetadata;
  }

  public void setSourceBlockMetadata(int sourceBlockMetadata) {
    this.sourceBlockMetadata = sourceBlockMetadata;
  }

  public Block getSourceBlock() {
    if(sourceBlockId == null) {
      return null;
    }
    return GameData.blockRegistry.get(sourceBlockId);
  }

  @Override
  public boolean canUpdate() {
    return false;
  }

}
