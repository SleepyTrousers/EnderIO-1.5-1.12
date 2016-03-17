package crazypants.enderio.machine.painter.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import crazypants.enderio.TileEntityEio;
import crazypants.enderio.paint.IPaintable;
import crazypants.enderio.paint.PainterUtil2;

public class TileEntityPaintedBlock extends TileEntityEio implements IPaintable.IPaintableTileEntity {

  private IBlockState paintSource = null;

  public TileEntityPaintedBlock() {
  }

  @Override
  public void readCustomNBT(NBTTagCompound nbtRoot) {
    this.paintSource = PainterUtil2.readNbt(nbtRoot);
  }

  @Override
  public void writeCustomNBT(NBTTagCompound nbtRoot) {
    PainterUtil2.writeNbt(nbtRoot, paintSource);
  }

  @Override
  public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    super.onDataPacket(net, pkt);
    updateBlock();
  }

  @Override
  public void setPaintSource(IBlockState paintSource) {
    this.paintSource = paintSource;
    markDirty();
    updateBlock();
  }

  @Override
  public IBlockState getPaintSource() {
    return paintSource;
  }

  public static class TileEntityTwicePaintedBlock extends TileEntityPaintedBlock {

    private IBlockState paintSource2 = null;

    @Override
    public void readCustomNBT(NBTTagCompound nbtRoot) {
      super.readCustomNBT(nbtRoot);
      this.paintSource2 = PainterUtil2.readNbt(nbtRoot.getCompoundTag("2"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbtRoot) {
      super.writeCustomNBT(nbtRoot);
      NBTTagCompound subTag = new NBTTagCompound();
      PainterUtil2.writeNbt(subTag, paintSource2);
      nbtRoot.setTag("2", subTag);
    }

    public void setPaintSource2(IBlockState paintSource2) {
      this.paintSource2 = paintSource2;
      markDirty();
      updateBlock();
    }

    public IBlockState getPaintSource2() {
      return paintSource2;
    }

  }

}
