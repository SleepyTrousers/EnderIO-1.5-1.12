package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import crazypants.util.BlockCoord;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.vecmath.Vector3d;

public class EnderRailController {

  //keep track of which carts we have received have left the track as they can't be teleported again until they have
  private final Set<EntityMinecart> newlySpawnedCarts = new HashSet<EntityMinecart>();

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
    List<EntityMinecart> toRemove = new ArrayList<EntityMinecart>();
    for (EntityMinecart recievedCart : newlySpawnedCarts) {
      if(!carts.contains(recievedCart)) {
        toRemove.add(recievedCart);
      }
    }
    for (EntityMinecart recievedCart : toRemove) {
      newlySpawnedCarts.remove(recievedCart);
    }
  }

  private void spawnRecievedCart() {

    if(cartsToSpawn.isEmpty()) {
      ticksFailedToSpawn = 0;
      return;
    }
    if(!isClear()) {
      ticksFailedToSpawn++;
      if(ticksFailedToSpawn < 60) {
        return;
      }
    }
    List<Entity> spawnThisTick = cartsToSpawn.removeFirst();
    for (Entity ent : spawnThisTick) {
      TeleportUtil.spawn(transciever.getWorldObj(), ent);
      if(ent instanceof EntityMinecart) {
        EntityMinecart cart = (EntityMinecart) ent;
        newlySpawnedCarts.add(cart);
        TeleportUtil.recreateLinks(cart);
      }
    }
    ticksFailedToSpawn = 0;

  }

  public void onTrainRecieved(List<List<Entity>> toTeleport) {
    cartsToSpawn.addAll(toTeleport);
  }

  public boolean isRecievedCart(EntityMinecart mc) {
    return newlySpawnedCarts.contains(mc);
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

  public void readFromNBT(NBTTagCompound root) {
    cartsToSpawn.clear();
    if(!root.hasKey("cartList")) {
      return;
    }
    cartList = (NBTTagList) root.getTag("cartList");
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
  }

}
