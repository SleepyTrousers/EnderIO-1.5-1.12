package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.ForgeDirection;

import com.enderio.core.common.util.BlockCoord;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.ForgeDirectionOffsets;
import com.enderio.core.common.vecmath.Vector3d;

import crazypants.enderio.config.Config;
import crazypants.enderio.machine.transceiver.TileTransceiver;

public class EnderRailController {

  //keep track of which carts we have received have left the track as they can't be teleported again until they have
  private final Set<UUID> newlySpawnedCarts = new HashSet<UUID>();

  private final LinkedList<List<Entity>> cartsToSpawn = new LinkedList<List<Entity>>();
  private final List<PlayerTpInfo> playersToRemount = new ArrayList<EnderRailController.PlayerTpInfo>();

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

    List<EntityMinecart> carts = getMinecartsOnTrack();
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

    if(Config.enderRailTeleportPlayers) {
      ListIterator<PlayerTpInfo> li = playersToRemount.listIterator();
      while (li.hasNext()) {
        PlayerTpInfo tpi = li.next();
        if(tpi.attemptMount() || tpi.attemptsRemaining <= 0) {
          li.remove();
        }
      }
    }

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
        EntityMinecart cart = null;
        if(ent instanceof EntityMinecart) {
          cart = (EntityMinecart) ent;
          setCartDirection(cart);
        }        
        TeleportUtil.spawn(transciever.getWorldObj(), ent);
        if(cart != null) {          
          newlySpawnedCarts.add(cart.getPersistentID());
          CartLinkUtil.instance.recreateLinks(cart);
        }
      }
    }

    ticksFailedToSpawn = 0;

  }

  private void setCartDirection(EntityMinecart cart) {
    int meta = transciever.getWorldObj().getBlockMetadata(transciever.xCoord, transciever.yCoord + 1, transciever.zCoord);
    ForgeDirection dir = BlockEnderRail.getDirection(meta);
    CartLinkUtil.instance.setCartDirection(cart, dir);
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

  public List<EntityMinecart> getMinecartsOnTrack() {
    return getMinecartsAt(transciever.getWorldObj(), transciever.xCoord, transciever.yCoord + 1, transciever.zCoord);
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
          //entity.onUpdate();
          worldserver.updateEntity(entity);
          return;
        }
      }
    }
    entity.setPosition(oX, entity.posY, oZ);
    resetForRandomRandomSpawn(entity);
    worldserver.spawnEntityInWorld(entity);
  }

  private void resetForRandomRandomSpawn(Entity entity) {
    CartLinkUtil.instance.breakLinks(transciever.getWorldObj(), entity);
    entity.riddenByEntity = null;
    entity.ridingEntity = null;
    entity.motionX = 0;
    entity.motionY = 0;
    entity.motionZ = 0;
    //    if(entity instanceof EntityMinecart) {
    //      entity.posY -= 0.3;      
    //    }
    entity.prevPosX = entity.posX;
    entity.prevPosY = entity.posY;
    entity.prevPosZ = entity.posZ;
    entity.rotationYaw = (float) (transciever.getWorldObj().rand.nextDouble() * 360);
  }

  private int randOffset(int spread) {
    return (int) Math.round((transciever.getWorldObj().rand.nextDouble() - 0.5) * spread * 2);
  }

  public void onPlayerTeleported(EntityPlayerMP playerToTP, EntityMinecart toMount) {
    if(playerToTP != null) {
      playersToRemount.add(new PlayerTpInfo(playerToTP.getCommandSenderName(), toMount.getPersistentID(), 20));
    }
  }

  private class PlayerTpInfo {
    String playerName;
    UUID cartId;
    int attemptsRemaining;

    PlayerTpInfo(String playerName, UUID cartId, int attemptsRemaining) {
      this.playerName = playerName;
      this.cartId = cartId;
      this.attemptsRemaining = attemptsRemaining;
    }

    boolean attemptMount() {
      attemptsRemaining--;
      List<EntityMinecart> carts = getMinecartsOnTrack();
      for (EntityMinecart cart : carts) {
        if(cart != null && cart.getPersistentID().equals(cartId)) {
          EntityPlayer player = transciever.getWorldObj().getPlayerEntityByName(playerName);
          if(player != null) {
            Vector3d playerPos = EntityUtil.getEntityPosition(player);
            Vector3d cartPos = EntityUtil.getEntityPosition(cart);
            if(playerPos.distanceSquared(cartPos) <= 9) {
              player.posX = cart.posX;
              player.posY = cart.posY;
              player.posZ = cart.posZ;
              player.mountEntity(cart);
              return true;
            }
          }
        }
      }
      return false;
    }

  }

}
