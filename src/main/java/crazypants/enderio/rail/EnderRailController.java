package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import codechicken.nei.WorldOverlayRenderer;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class EnderRailController {

  //keep track of which carts we have received have left the track as they can't be teleported again until they have
  private final Set<UUID> newlySpawnedCarts = new HashSet<UUID>();

  private final LinkedList<List<Entity>> cartsToSpawn = new LinkedList<List<Entity>>();

  private int ticksFailedToSpawn = 0;

  private NBTTagList cartList;

  private final TileTransceiver transciever;

  public EnderRailController(TileTransceiver tileTransceiver) {
    transciever = tileTransceiver;
  }

  public void doTick() {

    if(cartList != null) {
      loadCartsToSpawn();
      cartList = null;
    }

    spawnRecievedCart();

    List<EntityMinecart> carts = getMinecartsAt(transciever.getWorldObj(), transciever.xCoord, transciever.yCoord + 1, transciever.zCoord);
    List<UUID> toRemove = new ArrayList<UUID>();

    //any cart in the newly spawned list no longer on the track needs to be removed    
    for (UUID recievedCartUID : newlySpawnedCarts) {
      EntityMinecart minecart = getMinecartWthUUID(recievedCartUID, carts);
      if(minecart == null) {
        toRemove.add(recievedCartUID);
      }
    }
    for (UUID recievedCart : toRemove) {
      newlySpawnedCarts.remove(recievedCart);
    }
  }

  private EntityMinecart getMinecartWthUUID(UUID recievedCartUID, Collection<EntityMinecart> carts) {
    for (EntityMinecart cart : carts) {
      if(recievedCartUID.equals(cart.getPersistentID())) {
        return cart;
      }
    }
    return null;
  }

  private void spawnRecievedCart() {

    if(cartsToSpawn.isEmpty()) {
      ticksFailedToSpawn = 0;
      return;
    }
    boolean failedSpawn = false;
    if(!isClear()) {
      ticksFailedToSpawn++;
      if(ticksFailedToSpawn < Config.enderRailTicksBeforeForceSpawningLinkedCarts) {
        return;
      }
      failedSpawn = true;
    }
    List<Entity> spawnThisTick = cartsToSpawn.removeFirst();
    for (Entity ent : spawnThisTick) {
      if(failedSpawn) {
        doRandomSpawn(ent);
      } else {
        TeleportUtil.spawn(transciever.getWorldObj(), ent);
        if(ent instanceof EntityMinecart) {
          EntityMinecart cart = (EntityMinecart) ent;
          newlySpawnedCarts.add(cart.getPersistentID());
          TeleportUtil.recreateLinks(cart);
        }
      }
    }
    ticksFailedToSpawn = 0;

  }

  public void onTrainRecieved(List<List<Entity>> toTeleport) {
    cartsToSpawn.addAll(toTeleport);
  }

  public boolean isRecievedCart(EntityMinecart mc) {
    return newlySpawnedCarts.contains(mc.getPersistentID());
  }

  public boolean isClear() {
    World worldObj = transciever.getWorldObj();

    BlockCoord railCoord = new BlockCoord(transciever).getLocation(ForgeDirection.UP);
    int meta = worldObj.getBlockMetadata(railCoord.x, railCoord.y, railCoord.z);

    double buf = 1;
    ForgeDirection dir = BlockEnderRail.getDirection(meta);
    Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
    offset.scale(buf);
    offset.x = Math.abs(offset.x);
    offset.z = Math.abs(offset.z);
    List res = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(railCoord.x - offset.x, railCoord.y,
        railCoord.z - offset.z, railCoord.x + 1 + offset.x, railCoord.y + 1, railCoord.z + 1 + offset.z));
    return res == null || res.isEmpty();
  }

  public static List<EntityMinecart> getMinecartsAt(World world, int x, int y, int z) {
    List entities = world.getEntitiesWithinAABB(EntityMinecart.class, AxisAlignedBB.getBoundingBox(x, y, z, x + 1, y + 1, z + 1));
    List<EntityMinecart> carts = new ArrayList<EntityMinecart>();
    for (Object o : entities) {
      EntityMinecart cart = (EntityMinecart) o;
      if(!cart.isDead) {
        carts.add((EntityMinecart) o);
      }
    }
    return carts;
  }

  private void loadCartsToSpawn() {
    World worldObj = transciever.getWorldObj();
    while (cartList.tagCount() > 0) {
      NBTTagList entityList = (NBTTagList) cartList.removeTag(0);
      List<Entity> ents = new ArrayList<Entity>(entityList.tagCount());
      for (int i = 0; i < entityList.tagCount(); i++) {
        NBTTagCompound entityRoot = entityList.getCompoundTagAt(i);
        Entity entity = EntityList.createEntityFromNBT(entityRoot, worldObj);
        if(entity != null) {
          ents.add(entity);
        }
      }
      cartsToSpawn.add(ents);
    }
  }

  public void readFromNBT(NBTTagCompound root) {
    cartsToSpawn.clear();
    if(!root.hasKey("cartList")) {
      return;
    }
    cartList = (NBTTagList) root.getTag("cartList");

    newlySpawnedCarts.clear();
    if(root.hasKey("newlySpawnedCarts")) {
      NBTTagList spawnedCartList = (NBTTagList) root.getTag("newlySpawnedCarts");
      for (int i = 0; i < spawnedCartList.tagCount(); i++) {
        String uuisStr = spawnedCartList.getStringTagAt(i);
        newlySpawnedCarts.add(UUID.fromString(uuisStr));
      }
    }
  }

  public void writeToNBT(NBTTagCompound root) {
    if(cartsToSpawn.isEmpty()) {
      return;
    }
    NBTTagList cartList = new NBTTagList();

    for (List<Entity> entsInCart : cartsToSpawn) {
      if(entsInCart != null && !entsInCart.isEmpty()) {
        NBTTagList entityList = new NBTTagList();
        cartList.appendTag(entityList);
        for (Entity entity : entsInCart) {
          NBTTagCompound entRoot = new NBTTagCompound();
          entRoot.setString("id", EntityList.getEntityString(entity));
          entity.writeToNBT(entRoot);
          entityList.appendTag(entRoot);
        }
      }
    }
    root.setTag("cartList", cartList);

    if(!newlySpawnedCarts.isEmpty()) {
      NBTTagList spawnedCartList = new NBTTagList();
      for (UUID uuid : newlySpawnedCarts) {
        spawnedCartList.appendTag(new NBTTagString(uuid.toString()));
      }
      root.setTag("newlySpawnedCarts", spawnedCartList);
    }
  }

  public void dropNonSpawnedCarts() {    
    if(cartsToSpawn.isEmpty()) {
      return;
    }
    for (List<Entity> entList : cartsToSpawn) {
      for (Entity entity : entList) {
        doRandomSpawn(entity);
      }
    }
    cartsToSpawn.clear();

  }

  private void doRandomSpawn(Entity entity) {
    if(entity == null) {
      return;
    }
    double oX = entity.posX;
    double oZ = entity.posZ;
    World world = transciever.getWorldObj();
    MinecraftServer minecraftserver = MinecraftServer.getServer();
    WorldServer worldserver = minecraftserver.worldServerForDimension(world.provider.dimensionId);
    for (int i = 0; i < 4; i++) {
      int x = transciever.xCoord + randOffset(2);
      int y = transciever.yCoord + 1;
      int z = transciever.zCoord + randOffset(2);
      Block b = world.getBlock(x, y, z);
      entity.setPosition(x + 0.5, entity.posY, z + 0.5);
      if(world.canPlaceEntityOnSide(b, x, y, z, false, ForgeDirection.UP.ordinal(), entity, null)) {
        resetForRandomRandomSpawn(entity);
        if(worldserver.spawnEntityInWorld(entity)) {
          entity.onUpdate();
          return;
        }
      }
    }
    entity.setPosition(oX, entity.posY, oZ);
    resetForRandomRandomSpawn(entity);
    worldserver.spawnEntityInWorld(entity);
  }

  private void resetForRandomRandomSpawn(Entity entity) {
    TeleportUtil.breakLinks(transciever.getWorldObj(), entity);
    entity.riddenByEntity = null;
    entity.ridingEntity = null;
    entity.motionX = 0;
    entity.motionY = 0;    
    entity.motionZ = 0;
//    if(entity instanceof EntityMinecart) {
//      entity.posY -= 0.3;      
//    }
    entity.prevPosX  = entity.posX;
    entity.prevPosY = entity.posY;
    entity.prevPosZ = entity.posZ;    
    entity.rotationYaw = (float) (transciever.getWorldObj().rand.nextDouble() * 360);    
  }

  private int randOffset(int spread) {
    return (int) Math.round((transciever.getWorldObj().rand.nextDouble() - 0.5) * spread * 2);
  }

}
