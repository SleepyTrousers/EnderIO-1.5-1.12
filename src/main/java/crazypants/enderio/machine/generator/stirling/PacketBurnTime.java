package crazypants.enderio.machine.generator.stirling;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import crazypants.enderio.network.MessageTileEntity;
import crazypants.enderio.network.MessageTileNBT;

public class PacketBurnTime extends MessageTileEntity<TileEntityStirlingGenerator> {

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
  public void toBytes(ByteBuf buf) {
    super.toBytes(ctx, buf);
    buf.writeInt(burnTime);
    buf.writeInt(totalBurnTime);
  }

  @Override
  public void fromBytes(ByteBuf buf) {
    super.fromBytes(ctx, buf);
    burnTime = buf.readInt();
    totalBurnTime = buf.readInt();
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TileEntityStirlingGenerator tile) {
    tile.burnTime = burnTime;
    tile.totalBurnTime = totalBurnTime;
  }

}
