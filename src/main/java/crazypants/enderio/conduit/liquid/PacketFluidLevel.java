package crazypants.enderio.conduit.liquid;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.conduit.IConduitBundle;
import crazypants.enderio.conduit.TileConduitBundle;
import crazypants.enderio.network.AbstractPacketTileEntity;

public class PacketFluidLevel extends AbstractPacketTileEntity<TileEntity> {

  NBTTagCompound tc;

  public PacketFluidLevel() {
  }

  public PacketFluidLevel(ILiquidConduit conduit) {
    super(conduit.getBundle().getEntity());
    tc = new NBTTagCompound();
    conduit.writeToNBT(tc);
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.encode(ctx, buf);
    ByteBufUtils.writeTag(buf, tc);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    super.decode(ctx, buf);
    tc = ByteBufUtils.readTag(buf);
  }

  @Override
  protected void handleClientSide(EntityPlayer player, World worldObj, TileEntity tile) {
    if(tc == null || !(tile instanceof IConduitBundle)) {
      return;
    }
    IConduitBundle bundle = (IConduitBundle) tile;
    ILiquidConduit con = bundle.getConduit(ILiquidConduit.class);
    if(con == null) {
      return;
    }
    con.readFromNBT(tc, TileConduitBundle.NBT_VERSION);
  }

}
