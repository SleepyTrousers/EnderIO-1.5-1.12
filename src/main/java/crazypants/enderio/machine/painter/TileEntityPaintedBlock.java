package crazypants.enderio.machine.painter;

import crazypants.enderio.TileEntityEio;
import net.minecraft.block.Block;
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
        if (sourceBlock != null) {
            nbtRoot.setString(KEY_SOURCE_BLOCK_ID, Block.blockRegistry.getNameForObject(sourceBlock));
        }
        nbtRoot.setInteger(KEY_SOURCE_BLOCK_META, sourceBlockMetadata);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
        updateBlock();
    }

    @Override
    public Block getSourceBlock() {
        return sourceBlock;
    }

    @Override
    public void setSourceBlock(Block sourceBlock) {
        this.sourceBlock = sourceBlock;
    }

    @Override
    public int getSourceBlockMetadata() {
        return sourceBlockMetadata;
    }

    @Override
    public void setSourceBlockMetadata(int sourceBlockMetadata) {
        this.sourceBlockMetadata = sourceBlockMetadata;
    }

    @Override
    public boolean shouldUpdate() {
        return false;
    }
}
