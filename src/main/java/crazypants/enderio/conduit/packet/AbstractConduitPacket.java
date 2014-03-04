package crazypants.enderio.conduit.packet;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import crazypants.enderio.conduit.IConduit;
import crazypants.enderio.conduit.IConduitBundle;

public class AbstractConduitPacket<T extends IConduit> extends AbstractConduitBundlePacket {

  private ConTypeEnum conType;

  public AbstractConduitPacket() {
  }

  public AbstractConduitPacket(TileEntity tile, ConTypeEnum conType) {
    super(tile);
    this.conType = conType;

  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    buf.writeShort(conType.ordinal());
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    conType = ConTypeEnum.values()[buf.readShort()];
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, IConduitBundle tile) {
    super.handleClientSide(player, worldObj, tile);
    IConduit conduit = tile.getConduit(conType.getBaseType());
    if(conduit != null) {
      handleClientSide(player, worldObj, tile, (T) conduit);
    }
  }

  protected void handleClientSide(EntityPlayer player, World worldObj, IConduitBundle tile, T conduit) {
    handle(player, worldObj, tile, conduit);
  }

  @Override
  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile) {
    super.handleServerSide(player, worldObj, tile);
    IConduit conduit = tile.getConduit(conType.getBaseType());
    if(conduit != null) {
      handleServerSide(player, worldObj, tile, (T) conduit);
    }
  }

  protected void handleServerSide(EntityPlayer player, World worldObj, IConduitBundle tile, T conduit) {
    handle(player, worldObj, tile, conduit);
  }

  protected void handle(EntityPlayer player, World worldObj, IConduitBundle tile, T conduit) {

  }

}
