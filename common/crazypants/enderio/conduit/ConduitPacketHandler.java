package crazypants.enderio.conduit;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.conduit.item.IItemConduit;
import crazypants.enderio.conduit.item.ItemFilter;
import crazypants.enderio.conduit.liquid.ILiquidConduit;
import crazypants.enderio.conduit.power.IPowerConduit;
import crazypants.enderio.conduit.redstone.IInsulatedRedstoneConduit;
import crazypants.enderio.conduit.redstone.IRedstoneConduit;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.util.DyeColor;
import crazypants.util.PacketUtil;

public class ConduitPacketHandler implements IPacketProcessor {

  private static enum ConTypeEnum {

    POWER(IPowerConduit.class),
    FLUID(ILiquidConduit.class),
    ITEM(IItemConduit.class),
    REDSTONE(IRedstoneConduit.class);

    final Class<? extends IConduit> baseType;

    private ConTypeEnum(Class<? extends IConduit> baseType) {
      this.baseType = baseType;
    }

    public Class<? extends IConduit> getBaseType() {
      return baseType;
    }

    public static ConTypeEnum get(IConduit con) {
      Class<? extends IConduit> from = con.getBaseConduitType();
      for (ConTypeEnum ct : ConTypeEnum.values()) {
        if(ct.getBaseType() == from) {
          return ct;
        }
      }
      return null;
    }

  }

  @Override
  public boolean canProcessPacket(int packetID) {
    return PacketHandler.ID_CONDUIT_CON_MODE == packetID || packetID == PacketHandler.ID_CONDUIT_SIGNAL_COL
        || packetID == PacketHandler.ID_CONDUIT_EXTRACT_MODE || packetID == PacketHandler.ID_CONDUIT_ITEM_FILTER
        || packetID == PacketHandler.ID_CONDUIT_ITEM_LOOP || packetID == PacketHandler.ID_CONDUIT_ITEM_CHANNEL
        || packetID == PacketHandler.ID_CONDUIT_FLUID_LEVEL;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(PacketHandler.ID_CONDUIT_CON_MODE == packetID) {
      processConnectionModePacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_SIGNAL_COL) {
      processSignalColorPacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_EXTRACT_MODE) {
      processExtractionModePacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_ITEM_FILTER) {
      processItemFilterPacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_ITEM_LOOP) {
      processItemLoopPacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_ITEM_CHANNEL) {
      processItemChannelPacket(data, player);
    } else if(packetID == PacketHandler.ID_CONDUIT_FLUID_LEVEL) {
      processFluidConduitLevelPacket(data, player);
    }
  }

  public static Packet createFluidConduitLevelPacket(ILiquidConduit liquidConduit) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    IConduitBundle bundle = liquidConduit.getBundle();
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_FLUID_LEVEL);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);
      NBTTagCompound tc = new NBTTagCompound();
      liquidConduit.writeToNBT(tc);
      PacketUtil.writeNBTTagCompound(tc, dos);

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

  private void processFluidConduitLevelPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ILiquidConduit con = conBun.getConduit(ILiquidConduit.class);
    if(con == null) {
      //Log.warn("processFluidConduitLevelPacket: no fluid conduit exists in bundle when recieving packet.");
      return;
    }
    NBTTagCompound tc = PacketUtil.readNBTTagCompound(data);
    con.readFromNBT(tc);
  }

  public static Packet createItemChannelPacket(IItemConduit itemConduit, ForgeDirection dir, DyeColor col, boolean input) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    IConduitBundle bundle = itemConduit.getBundle();
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_ITEM_CHANNEL);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);
      dos.writeShort(dir.ordinal());
      dos.writeShort(col.ordinal());
      dos.writeBoolean(input);

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

  private void processItemChannelPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];

    IItemConduit con = conBun.getConduit(IItemConduit.class);
    if(con == null) {
      Log.warn("processItemChannelPacket.processItemLoopPacket: no item conduit exists in bundle when recieving packet.");
      return;
    }

    DyeColor col = DyeColor.values()[data.readShort()];
    boolean isInput = data.readBoolean();
    if(isInput) {
      con.setInputColor(dir, col);
    } else {
      con.setOutputColor(dir, col);
    }

  }

  private void processItemLoopPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];

    IItemConduit con = conBun.getConduit(IItemConduit.class);
    if(con == null) {
      Log.warn("ConduitPacketHandler.processItemLoopPacket: no item conduit exists in bundle when recieving packet.");
      return;
    }

    boolean loopEnabled = data.readBoolean();
    con.setSelfFeedEnabled(dir, loopEnabled);
    conBun.getEntity().onInventoryChanged();
    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);
  }

  public static Packet createItemLoopPacket(IItemConduit itemConduit, ForgeDirection dir) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    IConduitBundle bundle = itemConduit.getBundle();
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_ITEM_LOOP);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);
      dos.writeShort(dir.ordinal());
      dos.writeBoolean(itemConduit.isSelfFeedEnabled(dir));

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

  public static Packet createItemFilterPacket(IItemConduit conduit, ForgeDirection dir, boolean isInput, ItemFilter filter) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    IConduitBundle bundle = conduit.getBundle();
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_ITEM_FILTER);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);
      dos.writeShort(dir.ordinal());

      dos.writeBoolean(isInput);
      dos.writeBoolean(filter.isBlacklist());
      dos.writeBoolean(filter.isMatchMeta());
      dos.writeBoolean(filter.isMatchNBT());
      dos.writeBoolean(filter.isUseOreDict());
      dos.writeBoolean(filter.isSticky());

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

  private static void processItemFilterPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];

    IItemConduit con = conBun.getConduit(IItemConduit.class);
    if(con == null) {
      Log.warn("ConduitPacketHandler.processItemFilterPacket: no item conduit exists in bundle when recieving packet.");
      return;
    }

    boolean isInput = data.readBoolean();

    ItemFilter itemFilter;

    if(isInput) {
      itemFilter = con.getInputFilter(dir);
    } else {
      itemFilter = con.getOutputFilter(dir);
    }

    itemFilter.setBlacklist(data.readBoolean());
    itemFilter.setMatchMeta(data.readBoolean());
    itemFilter.setMatchNBT(data.readBoolean());
    itemFilter.setUseOreDict(data.readBoolean());
    itemFilter.setSticky(data.readBoolean());

    //Set it back so the conduit knows its changed
    if(isInput) {
      con.setInputFilter(dir, itemFilter);
    } else {
      con.setOutputFilter(dir, itemFilter);
    }

    conBun.getEntity().onInventoryChanged();
    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);

  }

  public static Packet createExtractionModePacket(IConduit conduit, ForgeDirection dir, RedstoneControlMode mode) {

    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    IConduitBundle bundle = conduit.getBundle();
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_EXTRACT_MODE);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);

      ConTypeEnum type = ConTypeEnum.get(conduit);
      if(type == null) {
        dos.writeShort(-1);
      } else {
        dos.writeShort(type.ordinal());
      }

      dos.writeShort(dir.ordinal());
      dos.writeShort(mode.ordinal());

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

  private static void processExtractionModePacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }

    int typeOrdinal = data.readShort();
    if(typeOrdinal < 0) {
      Log.warn("processConnectionModePacket: Could not handle due to unknow conduit type.");
      return;
    }
    ConTypeEnum conType = ConTypeEnum.values()[typeOrdinal];

    ForgeDirection dir = ForgeDirection.values()[data.readShort()];
    RedstoneControlMode mode = RedstoneControlMode.values()[data.readShort()];

    IConduit con = conBun.getConduit(conType.getBaseType());
    if(con == null) {
      Log.warn("processConnectionModePacket: Could not handle as conduit not found in bundle.");
      return;
    }

    //TODO: yeah, I know
    if(con instanceof ILiquidConduit) {
      ((ILiquidConduit) con).setExtractionRedstoneMode(mode, dir);
    } else if(con instanceof IItemConduit) {
      ((IItemConduit) con).setExtractionRedstoneMode(mode, dir);
    } else if(con instanceof IPowerConduit) {
      ((IPowerConduit) con).setRedstoneMode(mode, dir);
    }

    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);

  }

  public static Packet createSignalColorPacket(IConduit conduit, ForgeDirection dir, DyeColor color) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_SIGNAL_COL);
      IConduitBundle bundle = conduit.getBundle();
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);

      dos.writeShort(dir.ordinal());

      dos.writeShort(color.ordinal());

      dos.writeShort(ConTypeEnum.get(conduit).ordinal());

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

  private static void processSignalColorPacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];
    DyeColor col = DyeColor.values()[data.readShort()];

    ConTypeEnum type = ConTypeEnum.values()[data.readShort()];
    IConduit con = conBun.getConduit(type.baseType);
    //TODO: yeah, I know
    if(con instanceof IInsulatedRedstoneConduit) {
      ((IInsulatedRedstoneConduit) con).setSignalColor(dir, col);
    } else if(con instanceof ILiquidConduit) {
      ((ILiquidConduit) con).setExtractionSignalColor(dir, col);
    } else if(con instanceof IItemConduit) {
      ((IItemConduit) con).setExtractionSignalColor(dir, col);
    } else if(con instanceof IPowerConduit) {
      ((IPowerConduit) con).setSignalColor(dir, col);
    } else {
      Log.warn("processSignalColorPacket: Could not handle as conduit not found in bundle.");
      return;
    }

    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);

  }

  public static Packet createConnectionModePacket(IConduitBundle bundle, IConduit con, ForgeDirection dir) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_CONDUIT_CON_MODE);
      dos.writeInt(bundle.getEntity().xCoord);
      dos.writeInt(bundle.getEntity().yCoord);
      dos.writeInt(bundle.getEntity().zCoord);

      dos.writeShort(dir.ordinal());

      dos.writeShort(con.getConectionMode(dir).ordinal());

      ConTypeEnum type = ConTypeEnum.get(con);
      if(type == null) {
        dos.writeShort(-1);
      } else {
        dos.writeShort(type.ordinal());
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

  private static void processConnectionModePacket(DataInputStream data, Player player) throws IOException {
    World world = getWorld(player);
    if(world == null) {
      return;
    }
    IConduitBundle conBun = getConduitBundle(data, world);
    if(conBun == null) {
      return;
    }
    ForgeDirection dir = ForgeDirection.values()[data.readShort()];
    ConnectionMode conMode = ConnectionMode.values()[data.readShort()];

    int typeOrdinal = data.readShort();
    if(typeOrdinal < 0) {
      Log.warn("processConnectionModePacket: Could not handle due to unknow conduit type.");
      return;
    }
    ConTypeEnum conType = ConTypeEnum.values()[typeOrdinal];

    IConduit con = conBun.getConduit(conType.getBaseType());
    if(con != null) {
      if(con instanceof IInsulatedRedstoneConduit) { //yeah, I know
        ((IInsulatedRedstoneConduit) con).forceConnectionMode(dir, conMode);
      } else {
        con.setConnectionMode(dir, conMode);
      }
    } else {
      Log.warn("processConnectionModePacket: Could not handle as conduit not found in bundle.");
      return;
    }

    world.markBlockForUpdate(conBun.getEntity().xCoord, conBun.getEntity().yCoord, conBun.getEntity().zCoord);
  }

  private static IConduitBundle getConduitBundle(DataInputStream data, World world) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    TileEntity te = world.getBlockTileEntity(x, y, z);
    if(!(te instanceof IConduitBundle)) {
      Log.warn("processConnectionModePacket: Could not handle packet as TileEntity was not a Conduit Bundle.");
      return null;
    }
    IConduitBundle conBun = (IConduitBundle) te;
    return conBun;
  }

  private static World getWorld(Player player) {
    if(!(player instanceof EntityPlayer)) {
      Log.warn("processConnectionModePacket: Could not handle packet as player not an entity player.");
      return null;
    }
    World world = ((EntityPlayer) player).worldObj;
    if(world == null) {
      Log.warn("processConnectionModePacket: Could not handle packet as player world was null.");
      return null;
    }
    return world;
  }

}
