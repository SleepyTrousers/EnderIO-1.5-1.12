package crazypants.enderio.enderface;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.world.WorldServer;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import com.mojang.authlib.GameProfile;

import crazypants.enderio.network.IPacketEio;

public class PacketOpenRemoteUi implements IPacketEio {

  int x;
  int y;
  int z;

  public PacketOpenRemoteUi() {
  }

  public PacketOpenRemoteUi(int x, int y, int z) {
    this.x = x;
    this.y = y;
    this.z = z;
  }

  @Override
  public void encode(ChannelHandlerContext ctx, ByteBuf buffer) {
    buffer.writeInt(x);
    buffer.writeInt(y);
    buffer.writeInt(z);
  }

  @Override
  public void decode(ChannelHandlerContext ctx, ByteBuf buffer) {
    x = buffer.readInt();
    y = buffer.readInt();
    z = buffer.readInt();

  }

  @Override
  public void handleClientSide(EntityPlayer player) {
  }

  @Override
  public void handleServerSide(EntityPlayer p) {

    EntityPlayerMP player = (EntityPlayerMP) p;
    net.minecraft.inventory.Container c = player.openContainer;
    PlayerProxy pp = new PlayerProxy(player, x, y, z);
    EntityPlayerMP proxy = createPlayerProxy(player, pp);
    proxy.playerNetServerHandler = player.playerNetServerHandler;
    proxy.inventory = player.inventory;
    proxy.currentWindowId = player.currentWindowId;
    proxy.inventoryContainer = player.inventoryContainer;
    //proxy.mcServer = player.mcServer;
    proxy.openContainer = player.openContainer;
    //proxy.theItemInWorldManager = player.theItemInWorldManager;
    proxy.worldObj = player.worldObj;

    System.out.println("PacketOpenRemoteUi.handleServerSide: " + x + "," + y + "," + z);

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
    Class<?>[] argTypes = new Class[] { MinecraftServer.class, WorldServer.class, GameProfile.class, ItemInWorldManager.class };
    Object[] args = new Object[] { player.mcServer, player.worldObj, player.getGameProfile(), player.theItemInWorldManager };

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

      // TODO: Move to config? or use reflectioon helper?
      this.realObj = obj;
      interceptNames.add("openGui");
      interceptNames.add("func_146100_a");

      interceptNames.add("displayGUIBrewingStand");
      interceptNames.add("func_71017_a");
      interceptNames.add("func_146098_a");

      interceptNames.add("displayGUIChest");
      interceptNames.add("func_71007_a");

      interceptNames.add("displayGUIDispenser");
      interceptNames.add("func_71006_a");
      interceptNames.add("func_146102_a");

      interceptNames.add("displayGUIEnchantment");
      interceptNames.add("func_71002_c");

      interceptNames.add("displayGUIFurnace");
      interceptNames.add("func_71042_a");
      interceptNames.add("func_146101_a");

      interceptNames.add("displayGUIWorkbench");
      interceptNames.add("func_71058_b");

      interceptNames.add("displayGuiHopper"); // for when its added
      interceptNames.add("func_94064_a"); // displayGuiHopper

      interceptNames.add("displayGUIHopper");
      interceptNames.add("func_146093_a");

      beaconNames.add("displayGUIBeacon");
      beaconNames.add("func_82240_a");
      beaconNames.add("func_146104_a");

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
        realObj.getNextWindowId();

        realObj.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(realObj.currentWindowId, 7, par1TileEntityBeacon.getInventoryName(),
            par1TileEntityBeacon.getSizeInventory(), true));

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

        realObj.getNextWindowId();
        realObj.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(realObj.currentWindowId, 8, "Repairing", 9, true));

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
