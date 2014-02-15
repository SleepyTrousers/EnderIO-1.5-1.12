package crazypants.enderio.enderface;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.UnsignedBytes;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.network.FMLNetworkHandler;
import cpw.mods.fml.common.network.NetworkModHandler;
import cpw.mods.fml.common.network.OpenGuiPacket;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.EnderIO;
import crazypants.enderio.GuiHandler;
import crazypants.enderio.IPacketProcessor;
import crazypants.enderio.Log;
import crazypants.enderio.PacketHandler;
import crazypants.enderio.enderface.te.MeProxy;

public class EnderfacePacketProcessor implements IPacketProcessor {

  @Override
  public boolean canProcessPacket(int packetID) {
    return packetID == PacketHandler.ID_ENDERFACE || packetID == PacketHandler.ID_ME_TERMINAL;
  }

  @Override
  public void processPacket(int packetID, INetworkManager manager, DataInputStream data, Player player) throws IOException {
    if(packetID == PacketHandler.ID_ENDERFACE) {
      handleEnderfacePacket(data, manager, player);
    } else if(packetID == PacketHandler.ID_ME_TERMINAL) {
      handleMeTerminalPacket(data, manager, player);
    } else {
      Log.error("EnderfacePacketProcessor.processPacket: Unknown packet with id" + packetID);
    }
  }

  private void handleMeTerminalPacket(DataInputStream data, INetworkManager manager, Player p) throws IOException {
    if(!(p instanceof EntityPlayerMP)) {
      return;
    }
    EntityPlayerMP player = (EntityPlayerMP) p;
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();

    Container container = null;
    try {
      container = MeProxy.createMeTerminalContainer(player, x, y, z, false);
    } catch (Exception e) {
      Log.warn("EnderfacePacketProcessor.openRemoteGui: Could not open remote AE GUI. " + e);
    }
    openRemoteGui(player, GuiHandler.GUI_ID_ME_ACCESS_TERMINAL, player.worldObj, x, y, z, container);
  }

  public static Packet250CustomPayload createMePacket(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_ME_TERMINAL);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
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

  public static Packet250CustomPayload createPacketEnderface(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(PacketHandler.ID_ENDERFACE);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
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

  private void handleEnderfacePacket(DataInputStream data, INetworkManager manager, Player p) throws IOException {
    if(!(p instanceof EntityPlayerMP)) {
      return;
    }

    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();

    EntityPlayerMP player = (EntityPlayerMP) p;
    net.minecraft.inventory.Container c = player.openContainer;
    PlayerProxy pp = new PlayerProxy(player, x, y, z);
    EntityPlayerMP proxy = createPlayerProxy(player, pp);
    proxy.playerNetServerHandler = player.playerNetServerHandler;
    proxy.inventory = player.inventory;
    proxy.currentWindowId = player.currentWindowId;
    proxy.inventoryContainer = player.inventoryContainer;
    proxy.mcServer = player.mcServer;
    proxy.openContainer = player.openContainer;
    proxy.theItemInWorldManager = player.theItemInWorldManager;
    proxy.worldObj = player.worldObj;

    player.theItemInWorldManager.activateBlockOrUseItem(proxy, player.worldObj, null, x, y, z, 0, 0, 0, 0);
    player.theItemInWorldManager.thisPlayerMP = player;
    if(c != proxy.openContainer) {
      player.openContainer = proxy.openContainer;
    }
  }

  void openRemoteGui(EntityPlayerMP player, int modGuiId, World world, int x, int y, int z, Container container) {

    ModContainer mc = FMLCommonHandler.instance().findContainerFor(EnderIO.instance);
    NetworkModHandler nmh = FMLNetworkHandler.instance().findNetworkModHandler(mc);

    if(nmh != null) {

      if(container != null) {
        player.incrementWindowID();
        player.closeContainer();
        int windowId = player.currentWindowId;
        Packet250CustomPayload pkt = new Packet250CustomPayload();
        pkt.channel = "FML";
        OpenGuiPacket p = new OpenGuiPacket();
        byte[] packetData = p.generatePacket(windowId, nmh.getNetworkId(), modGuiId, x, y, z);
        byte[] b = Bytes.concat(new byte[] { UnsignedBytes.checkedCast(4) }, packetData);
        pkt.data = b;
        pkt.length = pkt.data.length;
        player.playerNetServerHandler.sendPacketToPlayer(pkt);
        player.openContainer = container;
        player.openContainer.windowId = windowId;
        player.openContainer.addCraftingToCrafters(player);
      }
    }
  }

  public static <T> T createPlayerProxy(EntityPlayerMP player, PlayerProxy proxy) {
    Enhancer e = new Enhancer();
    e.setCallback(proxy);

    e.setSuperclass(player.getClass());
    Class<?>[] argTypes = new Class[] { MinecraftServer.class, World.class, String.class, ItemInWorldManager.class };
    Object[] args = new Object[] { player.mcServer, player.worldObj, player.username, player.theItemInWorldManager };

    e.setInterceptDuringConstruction(false);
    @SuppressWarnings("unchecked")
    T proxifiedObj = (T) e.create(argTypes, args);

    return proxifiedObj;
  }

  public static class PlayerProxy implements MethodInterceptor {

    private EntityPlayerMP realObj;

    private Set<String> interceptNames = new HashSet<String>();

    private Set<String> beaconNames = new HashSet<String>();
    private Set<String> anvilNames = new HashSet<String>();

    private Set<String> distanceNames = new HashSet<String>();

    int x;
    int y;
    int z;

    public PlayerProxy(EntityPlayerMP obj, int x, int y, int z) {

      this.x = x;
      this.y = y;
      this.z = z;

      // TODO: Move to config?
      this.realObj = obj;
      interceptNames.add("openGui");

      interceptNames.add("displayGUIBrewingStand");
      interceptNames.add("func_71017_a");

      interceptNames.add("displayGUIChest");
      interceptNames.add("func_71007_a");

      interceptNames.add("displayGUIDispenser");
      interceptNames.add("func_71006_a");

      interceptNames.add("displayGUIEnchantment");
      interceptNames.add("func_71002_c");

      interceptNames.add("displayGUIFurnace");
      interceptNames.add("func_71042_a");

      interceptNames.add("displayGUIWorkbench");
      interceptNames.add("func_71058_b");

      interceptNames.add("displayGuiHopper"); // for when its added
      interceptNames.add("func_94064_a"); // displayGuiHopper

      interceptNames.add("displayGUIHopper");

      beaconNames.add("displayGUIBeacon");
      beaconNames.add("func_82240_a");

      anvilNames.add("displayGUIAnvil");
      anvilNames.add("func_82244_d");

      distanceNames.add("getDistanceSq");
      distanceNames.add("getDistance");
      distanceNames.add("func_70092_e");
      distanceNames.add("func_70011_f");
      distanceNames.add("func_71569_e");

    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

      if(interceptNames.contains(method.getName())) {

        Object res = method.invoke(realObj, objects);
        if(realObj.openContainer != null) {
          realObj.openContainer = new ContainerWrapper(realObj.openContainer);
        }
        return res;

      } else if(beaconNames.contains(method.getName())) {

        // Beacon needs to be done manually for some reason to work properly
        TileEntityBeacon par1TileEntityBeacon = (TileEntityBeacon) objects[0];
        realObj.incrementWindowID();
        realObj.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(realObj.currentWindowId, 7, par1TileEntityBeacon.getInvName(),
            par1TileEntityBeacon.getSizeInventory(), par1TileEntityBeacon.isInvNameLocalized()));
        realObj.openContainer = new ContainerBeacon(realObj.inventory, par1TileEntityBeacon) {
          @Override
          public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
            return true;
          }
        };
        realObj.openContainer.windowId = realObj.currentWindowId;
        realObj.openContainer.addCraftingToCrafters(realObj);
        return null;

      } else if(anvilNames.contains(method.getName())) {

        realObj.incrementWindowID();
        realObj.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(realObj.currentWindowId, 8, "Repairing", 9, true));
        realObj.openContainer = new ContainerRepair(realObj.inventory, realObj.worldObj, ((Integer) objects[0]).intValue(), ((Integer) objects[1]).intValue(),
            ((Integer) objects[2]).intValue(), realObj) {
          @Override
          public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
            return true;
          }
        };
        realObj.openContainer.windowId = realObj.currentWindowId;
        realObj.openContainer.addCraftingToCrafters(realObj);
        return null;
      } else if(distanceNames.contains(method.getName())) {
        return 6;
      }
      method.setAccessible(true);
      return method.invoke(realObj, objects);
    }

  }

}
