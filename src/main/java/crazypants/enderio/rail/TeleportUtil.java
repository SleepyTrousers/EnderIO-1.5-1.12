package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.vecmath.Vector3d;

public class TeleportUtil {

  public static List<Entity> createEntitiesForReciever(EntityMinecart cart, TileTransceiver sender, TileTransceiver reciever) {
    int toDimension = reciever.getWorldObj().provider.dimensionId;
    int toX = reciever.xCoord;
    int toY = reciever.yCoord + 1;
    int toZ = reciever.zCoord;

    MinecraftServer minecraftserver = MinecraftServer.getServer();
    WorldServer worldserver1 = minecraftserver.worldServerForDimension(toDimension);
    EntityMinecart newCart = (EntityMinecart) EntityList.createEntityByName(EntityList.getEntityString(cart), worldserver1);
    if(newCart == null) {
      return null;
    }

    NBTTagCompound nbttagcompound = new NBTTagCompound();
    cart.writeToNBT(nbttagcompound);
    newCart.readFromNBT(nbttagcompound);
    newCart.dimension = toDimension;
    newCart.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
    newCart.isDead = false;

    List<Entity> result = new ArrayList<Entity>();
    result.add(newCart);

    Entity passenger = cart.riddenByEntity;
    if(passenger != null && !(passenger instanceof EntityPlayer)) {
      Entity newPas = EntityList.createEntityByName(EntityList.getEntityString(passenger), worldserver1);
      newPas.copyDataFrom(passenger, true);
      newPas.dimension = toDimension;
      newPas.setLocationAndAngles(toX + 0.5, toY, toZ + 0.5, cart.rotationYaw, cart.rotationPitch);
      newCart.riddenByEntity = newPas;
      newPas.ridingEntity = newCart;
      result.add(newPas);
    }
    return result;
  }

  public static void despawn(World world, EntityMinecart cart) {

    if(cart instanceof IInventory) {
      IInventory cont = (IInventory) cart;
      for (int i = 0; i < cont.getSizeInventory(); i++) {
        cont.setInventorySlotContents(i, null);
      }
    }

    MinecraftServer minecraftserver = MinecraftServer.getServer();
    WorldServer worldserver = minecraftserver.worldServerForDimension(world.provider.dimensionId);

    Entity passenger = cart.riddenByEntity;
    if(passenger != null && !(passenger instanceof EntityPlayer)) {
      worldserver.removeEntity(passenger);
      passenger.isDead = true;
    }
    worldserver.removeEntity(cart);
    cart.isDead = true;

  }

  public static void spawn(World world, Entity entity ) {    
    if(entity != null) {      
      MinecraftServer minecraftserver = MinecraftServer.getServer();
      WorldServer worldserver = minecraftserver.worldServerForDimension(world.provider.dimensionId);
      worldserver.spawnEntityInWorld(entity);
    } 
  }

  //------------ Link Utils

  public static void breakLinks(World world, Entity cart) {
    if(cart instanceof EntityMinecart) {
      breakLinks(world, (EntityMinecart)cart);
    }
  }
  
  public static void breakLinks(World world, EntityMinecart cart) {
    if(world == null || cart == null) {
      return;
    }
    ILinkageManager linkMan = CartTools.getLinkageManager(world);
    if(linkMan == null) {
      return;
    }
    linkMan.breakLinks(cart);
  }
  
  public static void recreateLink(EntityMinecart existingCart, EntityMinecart newCart) {
    if(existingCart == null || newCart == null) {
      return;
    }
    ILinkageManager linkMan = CartTools.getLinkageManager(existingCart.worldObj);
    if(linkMan == null) {
      return;
    }    
    UUID linkA = getLinkA(newCart);
    if(linkA != null && linkA.equals(existingCart.getPersistentID())) {
      if(!linkMan.areLinked(existingCart, newCart)) {
        boolean res = linkMan.createLink(existingCart, newCart);      
      }
      return;
    }
    UUID linkB = getLinkB(newCart);
    if(linkB != null && linkB.equals(existingCart.getPersistentID())) {
      if(!linkMan.areLinked(existingCart, newCart)) {
        boolean res = linkMan.createLink(existingCart, newCart);
      }
      return;
    }
  }

  public static void updateCartLinks(World world, EntityMinecart cart) {
    ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
    if(linkMan == null || linkMan.countCartsInTrain(cart) <= 1) {
      return;
    }
    Iterable<EntityMinecart> allCarts = linkMan.getCartsInTrain(cart);
    for (EntityMinecart aCart : allCarts) {
      if(aCart != null) {
        updateLink("a", aCart, linkMan.getLinkedCartA(aCart));
        updateLink("b", aCart, linkMan.getLinkedCartB(aCart));
      }
    }
  }

  private static void updateLink(String prefix, EntityMinecart cart, EntityMinecart linkedCart) {
    NBTTagCompound data = cart.getEntityData();
    long lastUpdateTime = -1;
    String timeKey = prefix + "UpdateTime";
    if(data.hasKey(timeKey)) {
      lastUpdateTime = data.getLong(timeKey);
    }
    long curTime = cart.worldObj.getTotalWorldTime();
    if(lastUpdateTime > 0 && curTime - lastUpdateTime < 100) {
      return;
    }
    data.setLong(timeKey, curTime);
    data.setString(prefix + "Link", linkedCart == null ? "null" : linkedCart.getPersistentID().toString());
  }

  public static UUID getLinkA(EntityMinecart cart) {
    return getLink("a", cart);
  }

  public static UUID getLinkB(EntityMinecart cart) {
    return getLink("b", cart);
  }

  private static UUID getLink(String prefix, EntityMinecart cart) {
    NBTTagCompound data = cart.getEntityData();
    String uuidStr = data.getString(prefix + "Link");
    if(uuidStr == null || uuidStr.trim().isEmpty() || "null".equals(uuidStr)) {
      return null;
    }
    return UUID.fromString(uuidStr);
  }

  public static int getNumberOfCartsInTrain(EntityMinecart cart) {
    if(cart == null) {
      return 0;
    }
    ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
    if(linkMan == null) {
      return 1;
    }
    return linkMan.countCartsInTrain(cart);
  }

  public static List<EntityMinecart> getCartsInTrain(EntityMinecart cart) {
    if(cart == null) {
      Collections.emptyList();
    }
    List<EntityMinecart> result = new ArrayList<EntityMinecart>();
    ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
    if(linkMan == null) {
      result.add(cart);
      return result;
    }

    Iterable<EntityMinecart> iter = linkMan.getCartsInTrain(cart);
    for (EntityMinecart cartInTrain : iter) {
      if(cartInTrain != null) {
        result.add(cartInTrain);
      }
    }
    Collections.sort(result, new TrainOrderComparator(cart));
    return result;
  }

  public static void recreateLinks(EntityMinecart cart) {
    ILinkageManager linkMan = CartTools.getLinkageManager(cart.worldObj);
    if(linkMan == null) {
      return;
    }
    recreateLink(cart, linkMan, getLinkA(cart));
    recreateLink(cart, linkMan, getLinkB(cart));
  }

  private static void recreateLink(EntityMinecart cart, ILinkageManager linkMan, UUID uuid) {
    if(uuid != null) {
      EntityMinecart linkTo = linkMan.getCartFromUUID(uuid);
      if(!linkMan.areLinked(cart, linkTo)) {
        linkMan.createLink(cart, linkTo);
      }
    }
  }

  private static class TrainOrderComparator implements Comparator<EntityMinecart> {

    private final Vector3d refPoint = new Vector3d();
    private final Vector3d loc1 = new Vector3d();
    private final Vector3d loc2 = new Vector3d();
    private double d1;
    private double d2;

    TrainOrderComparator(EntityMinecart head) {
      set(refPoint, head);
    }

    @Override
    public int compare(EntityMinecart o1, EntityMinecart o2) {
      set(loc1, o1);
      set(loc2, o2);
      d1 = refPoint.distanceSquared(loc1);
      d2 = refPoint.distanceSquared(loc2);
      return Double.compare(d1, d2);
    }

    private void set(Vector3d point, EntityMinecart cart) {
      point.set(cart.posX, cart.posY, cart.posZ);
    }

  }

}
