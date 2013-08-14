package crazypants.enderio;

import java.awt.Container;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.item.ItemInWorldManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.World;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import crazypants.enderio.enderface.ContainerWrapper;
import crazypants.enderio.machine.AbstractMachineEntity;
import crazypants.enderio.machine.RedstoneControlMode;
import crazypants.util.PacketUtil;

public class PacketHandler implements IPacketHandler {

  public static int ID_ENDERFACE = 1;
  private static final int ID_TILE_ENTITY = 4;
  private static final int ID_MACHINE_REDSTONE_PACKET = 2;

  private static final String CHANNEL = "EnderIO";
  

  @Override
  public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player player) {
    if (packet.data != null && packet.data.length <= 0) {
      return;
    }

    DataInputStream data = new DataInputStream(new ByteArrayInputStream(packet.data));
    try {
      int id = data.readInt();
      if (id == ID_ENDERFACE) {
        handleEnderfacePacket(data, manager, player);   
      } else if(id == ID_TILE_ENTITY) {   
        PacketUtil.handleTileEntityPacket(false, data);
      } else if(id == ID_MACHINE_REDSTONE_PACKET) {
        handleRedstoneControlPacket(data, manager, player);
      } else {      
        System.out.println("PacketHandler.onPacketData: Recieved packet of unknown type: " + id);
      }
    } catch (IOException ex) {
      FMLCommonHandler.instance().raiseException(ex, "PacketHandler.onPacketData", true);
    }
  }

  

  public static Packet getPacket(TileEntity te) {
    return PacketUtil.createTileEntityPacket(CHANNEL, ID_TILE_ENTITY, te);
  }

  // ---------------- Redstone Control --------------------------------------------------------

  private void handleRedstoneControlPacket(DataInputStream data, INetworkManager manager, Player player) throws IOException {
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();
    short ordinal = data.readShort();
    EntityPlayerMP p = (EntityPlayerMP) player;
    TileEntity te = p.worldObj.getBlockTileEntity(x, y, z);
    if(te instanceof AbstractMachineEntity) {
      AbstractMachineEntity me = (AbstractMachineEntity)te;
      me.setRedstoneControlMode(RedstoneControlMode.values()[ordinal]);
      p.worldObj.markBlockForUpdate(x, y, z);      
    }
    
  }
  
  public static Packet getRedstoneControlPacket(AbstractMachineEntity te) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(140);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(ID_MACHINE_REDSTONE_PACKET);
      dos.writeInt(te.xCoord);
      dos.writeInt(te.yCoord);
      dos.writeInt(te.zCoord);
      dos.writeShort((short)te.getRedstoneControlMode().ordinal());
    } catch (IOException e) {
      // never thrown
    }

    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
    
  }


  // ---------------- Enderface
  // ------------------------------------------------------------

  public static Packet250CustomPayload getPacketEnderface(int x, int y, int z) {
    ByteArrayOutputStream bos = new ByteArrayOutputStream(16);
    DataOutputStream dos = new DataOutputStream(bos);
    try {
      dos.writeInt(ID_ENDERFACE);
      dos.writeInt(x);
      dos.writeInt(y);
      dos.writeInt(z);
    } catch (IOException e) {
      // never thrown
    }
    Packet250CustomPayload pkt = new Packet250CustomPayload();
    pkt.channel = CHANNEL;
    pkt.data = bos.toByteArray();
    pkt.length = bos.size();
    pkt.isChunkDataPacket = true;
    return pkt;
  }

  private void handleEnderfacePacket(DataInputStream data, INetworkManager manager, Player p) throws IOException {
    if (!(p instanceof EntityPlayerMP)) {
      System.out.println("kitchenbench.PacketHandler.handleEnderfacePacket: <ERROR> Not an EntityPlayerMP");
      return;
    }
    int x = data.readInt();
    int y = data.readInt();
    int z = data.readInt();

    EntityPlayerMP player = (EntityPlayerMP) p;
    net.minecraft.inventory.Container c = player.openContainer;
    PlayerProxy pp = new PlayerProxy(player);
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

    public PlayerProxy(EntityPlayerMP obj) {
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
      
      
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
      if (interceptNames.contains(method.getName())) {
        
        Object res = method.invoke(realObj, objects);
        if (realObj.openContainer != null) {
          realObj.openContainer = new ContainerWrapper(realObj.openContainer);
        }
        return res;
        
      } else if (beaconNames.contains(method.getName())) {
        
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
        
      } else if (anvilNames.contains(method.getName())) {
        
        realObj.incrementWindowID();
        realObj.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(realObj.currentWindowId, 8, "Repairing", 9, true));
        realObj.openContainer = new ContainerRepair(realObj.inventory, realObj.worldObj,  ((Integer)objects[0]).intValue(),  ((Integer)objects[1]).intValue(),  ((Integer)objects[2]).intValue(), realObj) {
          @Override
          public boolean canInteractWith(EntityPlayer par1EntityPlayer) {
            return true;
          }
        };
        realObj.openContainer.windowId = realObj.currentWindowId;
        realObj.openContainer.addCraftingToCrafters(realObj);
        return null;
     
      } else if(method.getName().equals("getDistanceSq")) { //for forestry
        return 6;
      }
      method.setAccessible(true);
      return method.invoke(realObj, objects);
    }    
    
  }

}
