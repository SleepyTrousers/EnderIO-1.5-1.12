package crazypants.enderio.machine.generator.stirling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.network.AbstractPacketTileEntity;
import crazypants.enderio.network.PacketTileEntityNbt;

public class PacketBurnTime extends AbstractPacketTileEntity<TileEntityStirlingGenerator> {

  private int burnTime;
  private int totalBurnTime;
  
  public PacketBurnTime() {
  }

  public PacketBurnTime(TileEntityStirlingGenerator tile) {
    super(tile);
    burnTime = tile.burnTime;
    totalBurnTime = tile.totalBurnTime;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeInt(burnTime);
    buf.writeInt(totalBurnTime);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    burnTime = buf.readInt();
    totalBurnTime = buf.readInt();
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TileEntityStirlingGenerator tile) {
    tile.burnTime = burnTime;
    tile.totalBurnTime = totalBurnTime;
  }

}
