package crazypants.enderio.machine.hypercube;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.network.ByteBufUtils;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.network.IPacketEio;

public class PacketClientState implements IPacketEio {

  private int x;
  private int y;
  private int z;
  private List<IoMode> modes;
  private Channel selectedChannel;

  public PacketClientState() {
  }

  public PacketClientState(TileHyperCube te) {
    x = te.xCoord;
    y = te.yCoord;
    z = te.zCoord;
    modes = new ArrayList<TileHyperCube.IoMode>(SubChannel.values().length);
    for (SubChannel sc : SubChannel.values()) {
      modes.add(te.getModeForChannel(sc));
    }
    selectedChannel = te.getChannel();
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buf) {
    buf.writeInt(x);
    buf.writeInt(y);
    buf.writeInt(z);

    if(selectedChannel == null) {
      ByteBufUtils.writeUTF8String(buf, "");
      ByteBufUtils.writeUTF8String(buf, "");
    } else {
      ByteBufUtils.writeUTF8String(buf, selectedChannel.name);
      if(selectedChannel.isPublic()) {
        ByteBufUtils.writeUTF8String(buf, "");
      } else {
        ByteBufUtils.writeUTF8String(buf, selectedChannel.user);
      }
    }

    for (IoMode mode : modes) {
      buf.writeShort(mode.ordinal());
    }
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buf) {
    x = buf.readInt();
    y = buf.readInt();
    z = buf.readInt();

    String name = ByteBufUtils.readUTF8String(buf);
    String user = ByteBufUtils.readUTF8String(buf);
    selectedChannel = null;
    if(name != null && name.trim().length() > 0) {
      if(user != null && user.trim().length() > 0) {
        selectedChannel = new Channel(name, user);
      } else {
        selectedChannel = new Channel(name, null);
      }

    }

    modes = new ArrayList<TileHyperCube.IoMode>(SubChannel.values().length);
    for (SubChannel sc : SubChannel.values()) {
      short ordinal = buf.readShort();
      modes.add(IoMode.values()[ordinal]);
    }

  }

  @Override
  public void handleClientSide(EntityPlayer player) {
  }

  @Override
  public void handleServerSide(EntityPlayer player) {

    TileEntity te = player.worldObj.getTileEntity(x, y, z);
    if(te instanceof TileHyperCube) {
      TileHyperCube hc = (TileHyperCube) te;

      SubChannel[] vals = SubChannel.values();
      for (int i = 0; i < vals.length; i++) {
        SubChannel sc = vals[i];
        IoMode mode = modes.get(i);
        hc.setModeForChannel(sc, mode);
      }

      hc.setChannel(selectedChannel);

      player.worldObj.markBlockForUpdate(x, y, z);
    }

  }

}
