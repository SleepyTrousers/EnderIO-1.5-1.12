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

  //  public static Packet createFluidConduitLevelPacket(ILiquidConduit liquidConduit) {
  //    ByteArrayOutputStream bos = new ByteArrayOutputStream();
  //    DataOutputStream dos = new DataOutputStream(bos);
  //    IConduitBundle bundle = liquidConduit.getBundle();
  //    try {
  //      dos.writeInt(PacketHandler.ID_CONDUIT_FLUID_LEVEL);
  //      dos.writeInt(bundle.getEntity().xCoord);
  //      dos.writeInt(bundle.getEntity().yCoord);
  //      dos.writeInt(bundle.getEntity().zCoord);
  //      NBTTagCompound tc = new NBTTagCompound();
  //      liquidConduit.writeToNBT(tc);
  //      PacketUtil.writeNBTTagCompound(tc, dos);
  //
  //    } catch (IOException e) {
  //      // never thrown
  //    }
  //    Packet250CustomPayload pkt = new Packet250CustomPayload();
  //    pkt.channel = PacketHandler.CHANNEL;
  //    pkt.data = bos.toByteArray();
  //    pkt.length = bos.size();
  //    pkt.isChunkDataPacket = true;
  //    return pkt;
  //  }
  //
  //  private void processFluidConduitLevelPacket(DataInputStream data, Player player) throws IOException {
  //    World world = getWorld(player);
  //    if(world == null) {
  //      return;
  //    }
  //    IConduitBundle conBun = getConduitBundle(data, world);
  //    if(conBun == null) {
  //      return;
  //    }
  //    ILiquidConduit con = conBun.getConduit(ILiquidConduit.class);
  //    if(con == null) {
  //      //Log.warn("processFluidConduitLevelPacket: no fluid conduit exists in bundle when recieving packet.");
  //      return;
  //    }
  //    NBTTagCompound tc = PacketUtil.readNBTTagCompound(data);
  //    con.readFromNBT(tc, TileConduitBundle.NBT_VERSION);
  //  }

}
