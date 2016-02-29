package crazypants.enderio.machine.painter;

import crazypants.enderio.TileEntityEio;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;

public class TileEntityPaintedBlock extends TileEntityEio implements IPaintableTileEntity {

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
      nbtRoot.setString(KEY_SOURCE_BLOCK_ID, Block.blockRegistry.getNameForObject(sourceBlock).toString());
    }
    nbtRoot.setInteger(KEY_SOURCE_BLOCK_META, sourceBlockMetadata);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    updateBlock();
  }

  @Override
  public void setSourceBlock(IBlockState source) {
    if(source == null) {
      sourceBlock = null;
      sourceBlockMetadata = 0;
    } else {
      sourceBlock = source.getBlock();
      sourceBlockMetadata = sourceBlock.getMetaFromState(source);
    }
    
  }

  @Override
  public IBlockState getSourceBlock() {
    if(sourceBlock == null) {
      return null;
    }
    return sourceBlock.getStateFromMeta(sourceBlockMetadata);
  }

}
