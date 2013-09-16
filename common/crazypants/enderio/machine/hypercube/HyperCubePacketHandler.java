package crazypants.enderio.machine.hypercube;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.NetLoginHandler;
import net.minecraft.network.packet.NetHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.machine.RedstoneControlMode;

public class HyperCubePacketHandler implements IPacketProcessor, IConnectionHandler {


  @Override
  public boolean canProcessPacket(int packetID) {
    return PacketHandler.ID_HYPER_CUBE_REDSTONE_PACKET == packetID || PacketHandler.ID_HYPER_CUBE_PUBLIC_CHANNEL_LIST == packetID
        || PacketHandler.ID_HYPER_CUBE_ADD_REMOVE_CHANNEL == packetID || PacketHandler.ID_HYPER_CUBE_PRIVATE_CHANNEL_LIST == packetID || PacketHandler.ID_HYPER_CUBE_CHANNEL_SELECTED == packetID;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if (packetID == PacketHandler.ID_HYPER_CUBE_REDSTONE_PACKET) {
      handleHyperCubeRedstoneControlPacket(data, manager, player);
    } else if (packetID == PacketHandler.ID_HYPER_CUBE_PUBLIC_CHANNEL_LIST) {
      handlePublicChannelListPacket(data, manager, player);
    } else if (packetID == PacketHandler.ID_HYPER_CUBE_ADD_REMOVE_CHANNEL) {
      handleAddRemoveChannelPacket(data, manager, player);
    } else if (packetID == PacketHandler.ID_HYPER_CUBE_PRIVATE_CHANNEL_LIST) {
      handlePrivateChannelPacket(data, manager, player);
    } else if(packetID == PacketHandler.ID_HYPER_CUBE_CHANNEL_SELECTED) {
      handleChannelSelectedPacket(data, manager, player);
    }
  }

  public static Packet createChannelSelectedPacket(TileHyperCube cube, Channel channel) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_HYPER_CUBE_CHANNEL_SELECTED);
      dos.writeInt(cube.xCoord);
      dos.writeInt(cube.yCoord);
      dos.writeInt(cube.zCoord);
      dos.writeBoolean(channel != null);      
      if (channel != null) {
        dos.writeBoolean(channel.isPublic());
        dos.writeUTF(channel.name);
        if (!channel.isPublic()) {
          dos.writeUTF(channel.user);
        }
      }
    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }
  
  private void handleChannelSelectedPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    if(! (player instanceof EntityPlayer) ) {
      FMLLog.warning("handleChannelSelectedPacket: Could not handle packet as player not an entity player.");
      return;
    }    
    World world = ((EntityPlayer)player).worldObj;
    if(world == null) {
      FMLLog.warning("handleChannelSelectedPacket: Could not handle packet as player world was null.");
      return;
    }
    
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(! (te instanceof TileHyperCube)) {
      FMLLog.warning("handleChannelSelectedPacket: Could not handle packet as TileEntity was not a HyperCube.");
      return;
    }
    TileHyperCube hc = (TileHyperCube)te;
    boolean hasCan = data.readBoolean();
    if(!hasCan) {
      hc.setChannel(null);
      return;
    }
    boolean isPublic = data.readBoolean();
    String name = data.readUTF();
    String user = null;
    if(!isPublic) {
      user = data.readUTF();
    }
    hc.setChannel(new Channel(name, user));        
  }

  public static Packet createUserChannelListPacket(String username) {

    List<Channel> channels = HyperCubeRegister.instance.getChannelsForUser(username);
    if (channels.isEmpty()) {
      return null;
    }
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_HYPER_CUBE_PRIVATE_CHANNEL_LIST);
      dos.writeUTF(username);
      dos.writeInt(channels.size());
      for (Channel channel : channels) {
        dos.writeUTF(channel.name);
      }
    } catch (IOException e) {
      // never thrown
    }

    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;

  }
  
  private void handlePrivateChannelPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {

    String user = data.readUTF();
    int numChannels = data.readInt();
    List<Channel> channels = new ArrayList<Channel>();
    for (int i = 0; i < numChannels; i++) {
      channels.add(new Channel(data.readUTF(), user));
    }
    ClientChannelRegister.instance.setPrivateChannels(channels);
  }

  
  public static Packet createAddRemoveChannelPacket(Channel channel, boolean isAdd) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_HYPER_CUBE_ADD_REMOVE_CHANNEL);
      dos.writeBoolean(isAdd);
      dos.writeBoolean(channel.isPublic());
      dos.writeUTF(channel.name);
      if (!channel.isPublic()) {
        dos.writeUTF(channel.user);
      }

    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }
  
  private void handleAddRemoveChannelPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    boolean isAdd = data.readBoolean();
    boolean isPublic = data.readBoolean();
    String name = data.readUTF();
    String user = null;
    if (!isPublic) {
      user = data.readUTF();
    }
    Channel channel = new Channel(name, user);
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    if (side == Side.SERVER) {
      if(isAdd) {
        HyperCubeRegister.instance.addChannel(channel);
      } else {
        HyperCubeRegister.instance.removeChannel(channel);
      }
      PacketDispatcher.sendPacketToAllPlayers(createAddRemoveChannelPacket(channel, isAdd));
    } else {
      if(isAdd) {
        ClientChannelRegister.instance.channelAdded(channel);
      } else {
        ClientChannelRegister.instance.channelRemoved(channel);
      }
    }
  }

  
  public static Packet createPublicChannelListPacket() {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    List<Channel> channels = HyperCubeRegister.instance.getPublicChannels();
    try {
      dos.writeInt(PacketHandler.ID_HYPER_CUBE_PUBLIC_CHANNEL_LIST);
      dos.writeInt(channels.size());
      for (Channel channel : channels) {
        dos.writeUTF(channel.name);
      }
    } catch (IOException e) {
      // never thrown
    }

    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }
  
  private void handlePublicChannelListPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int numChannels = data.readInt();
    List<Channel> channels = new ArrayList<Channel>();
    for (int i = 0; i < numChannels; i++) {
      channels.add(new Channel(data.readUTF(), null));
    }
    ClientChannelRegister.instance.setPublicChannels(channels);
  }

  
  public static Packet createRedstoneControlPacket(TileHyperCube te) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_HYPER_CUBE_REDSTONE_PACKET);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeShort((short) te.getInputControlMode().ordinal());
      dos.writeShort((short) te.getOutputControlMode().ordinal());
    } catch (IOException e) {
      // never thrown
    }

    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = PacketHandler.CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }
  
  private void handleHyperCubeRedstoneControlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    short powerInputOrdinal = data.readShort();
    short powerOutputOrdinal = data.readShort();
    EntityPlayerMP p = (EntityPlayerMP) player;
    TileEntity te = p.worldObj.getBlockTileEntity(x, y, z);
    if (te instanceof TileHyperCube) {
      TileHyperCube cb = (TileHyperCube) te;
      cb.setInputControlMode(RedstoneControlMode.values()[powerInputOrdinal]);
      cb.setOutputControlMode(RedstoneControlMode.values()[powerOutputOrdinal]);
      p.worldObj.markBlockForUpdate(x, y, z);
    }

  }

  @Override
  public void playerLoggedIn(Player player, NetHandler netHandler, INetworkManager manager) {
    Packet pkt = createPublicChannelListPacket();
    PacketDispatcher.sendPacketToPlayer(pkt, player);

    if (player instanceof EntityPlayerMP) {
      EntityPlayerMP ep = (EntityPlayerMP) player;
      pkt = createUserChannelListPacket(ep.username);
      if (pkt != null) {
        PacketDispatcher.sendPacketToPlayer(pkt, player);
      }
    } else {

      FMLLog.warning("HyperCubePacketHandler.playerLoggedIn: Could not determine player user name");
    }

  }

  @Override
  public String connectionReceived(NetLoginHandler netHandler, INetworkManager manager) {
    return null;
  }

  @Override
  public void connectionOpened(NetHandler netClientHandler, String server, int port, INetworkManager manager) {
  }

  @Override
  public void connectionOpened(NetHandler netClientHandler, MinecraftServer server, INetworkManager manager) {
  }

  @Override
  public void connectionClosed(INetworkManager manager) {
    Side side = FMLCommonHandler.instance().getEffectiveSide();
    if (side == Side.CLIENT) {
      ClientChannelRegister.instance.reset();
    }

  }

  @Override
  public void clientLoggedIn(NetHandler clientHandler, INetworkManager manager, Packet1Login login) {
  }

}
