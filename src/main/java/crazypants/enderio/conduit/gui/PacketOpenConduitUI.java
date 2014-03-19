package crazypants.enderio.conduit.gui;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.network.AbstractPacketTileEntity;

public class PacketOpenConduitUI extends AbstractPacketTileEntity<TileEntity> {

  private ForgeDirection dir;

  public PacketOpenConduitUI() {
  }

  public PacketOpenConduitUI(TileEntity tile, ForgeDirection dir) {
    super(tile);
    this.dir = dir;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(dir.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    dir = ForgeDirection.values()[buf.readShort()];
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, TileEntity tile) {
    player.openGui(EnderIO.instance, GuiHandler.GUI_ID_EXTERNAL_CONNECTION_BASE + dir.ordinal(), player.worldObj, tile.xCoord, tile.yCoord, tile.zCoord);
  }

}
