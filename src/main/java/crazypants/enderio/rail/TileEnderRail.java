package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import crazypants.enderio.Log;
import crazypants.enderio.TileEntityEio;
import crazypants.util.BlockCoord;
import crazypants.util.EntityUtil;
import crazypants.util.ForgeDirectionOffsets;
import crazypants.util.MetadataUtil;
import crazypants.vecmath.Vector3d;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnderRail extends TileEntityEio {

  //keep track of which carts we have received have left the track as they can't be teleported again until they have
  private final Set<EntityMinecart> newlySpawnedCarts = new HashSet<EntityMinecart>();  
  
  private final LinkedList<List<Entity>> cartsToSpawn = new LinkedList<List<Entity>>();
  
  private int ticksFailedToSpawn = 0;
  
//  private NBTTagList cartList;
  
  public TileEnderRail() {
  }
  
  @Override
  public void updateEntity() {
    
//    if(cartList != null) {
//      loadCartsToSpawn();
//      cartList = null;
//    }
    
    spawnRecievedCart();
    
    List<EntityMinecart> carts = getMinecartsAt(getWorldObj(), xCoord, yCoord, zCoord);
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
//    System.out.println("TileEnderRail.spawnRecievedCart: " + cartsToSpawn.size() + ":" + new BlockCoord(this));
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
    System.out.println("TileEnderRail.spawnRecievedCart: " + cartsToSpawn);
    
    List<Entity> spawnThisTick = cartsToSpawn.removeFirst();        
    for(Entity ent : spawnThisTick) {
      TeleportUtil.spawn(getWorldObj(), ent);
      if(ent instanceof EntityMinecart) {
        EntityMinecart cart = (EntityMinecart)ent;
        newlySpawnedCarts.add(cart);
        TeleportUtil.recreateLinks(cart);
      }      
    }          
    ticksFailedToSpawn = 0;
    
  }

//  public boolean isReverse() {
//    return BlockEnderRail.isReverse(getBlockMetadata());
//  }
//
//  public ForgeDirection getDirection() {
//    return BlockEnderRail.getDirection(getBlockMetadata());
//  }

  public void onTrainRecieved(List<List<Entity>> toTeleport) {    
//    System.out.println("TileEnderRail.onTrainRecieved: " + toTeleport.size() + ":" + System.identityHashCode(this) + ":" + new BlockCoord(this));
    cartsToSpawn.addAll(toTeleport);
//    System.out.println("TileEnderRail.onTrainRecieved: Carts to spawn now " + cartsToSpawn.size() + ":" + new BlockCoord(this));
  }
  
  public boolean isRecievedCart(EntityMinecart mc) {
    return newlySpawnedCarts.contains(mc);
  }

  public boolean isClear() {
    double buf = 1;
    ForgeDirection dir = BlockEnderRail.getDirection(getBlockMetadata());
    Vector3d offset = ForgeDirectionOffsets.forDirCopy(dir);
    offset.scale(buf);
    offset.x = Math.abs(offset.x);
    offset.z = Math.abs(offset.z);
    List res = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord - offset.x, yCoord, zCoord - offset.z, xCoord + 1 + offset.x, yCoord + 1, zCoord + 1 + offset.z));
    return res == null || res.isEmpty();
  }

  public List<EntityMinecart> getCartsOnTrack() {
    return getMinecartsAt(getWorldObj(), xCoord, yCoord + 1, zCoord);
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

//  @Override
//  public void readFromNBT(NBTTagCompound root) {
//    
//    super.readFromNBT(root);    
//    cartsToSpawn.clear();
//    if(!root.hasKey("cartList")) {
//      System.out.println("TileEnderRail.readFromNBT: No cart list found");
//      return;
//    }    
//    cartList = (NBTTagList)root.getTag("cartList");    
//    System.out.println("TileEnderRail.readFromNBT: read cart list with " + cartList.tagCount() + " entries");
//  }
  
//  private void loadCartsToSpawn() {
//    System.out.println("TileEnderRail.loadCartsToSpawn: Loading carts to spawn: " + cartList.tagCount());
//    while(cartList.tagCount() > 0) {
//      NBTTagList entityList = (NBTTagList)cartList.removeTag(0);
//      System.out.println("TileEnderRail.loadCartsToSpawn: Entity list size: " + entityList.tagCount());
//      List<Entity> ents = new ArrayList<Entity>(entityList.tagCount());
//      for(int i=0;i < entityList.tagCount();i++) {
//        NBTTagCompound entityRoot = entityList.getCompoundTagAt(i);        
//        Entity entity = EntityList.createEntityFromNBT(entityRoot, worldObj);
//        if(entity != null) {
//          ents.add(entity);
//          System.out.println("TileEnderRail.loadCartsToSpawn: Added entity to list: " + entity);
//        } else {
//          System.out.println("TileEnderRail.loadCartsToSpawn: Null emtity");
//        }
//      }
//      cartsToSpawn.add(ents);  
//    }    
//  }

//  @Override
//  public void writeToNBT(NBTTagCompound root) {
//    super.writeToNBT(root);
//    if(cartsToSpawn.isEmpty()) {
//      return;
//    }
//    NBTTagList cartList = new NBTTagList();
//    
//    for(List<Entity> entsInCart : cartsToSpawn) {
//      if(entsInCart != null && !entsInCart.isEmpty()) {        
//        NBTTagList entityList = new NBTTagList();
//        cartList.appendTag(entityList);
//        for(Entity entity : entsInCart) {
//          NBTTagCompound entRoot = new NBTTagCompound();         
//          entRoot.setString("id", EntityList.getEntityString(entity));
//          entity.writeToNBT(entRoot);
//          entityList.appendTag(entRoot);
//        }
//      }
//    }    
//    root.setTag("cartList", cartList);
//    System.out.println("TileEnderRail.writeToNBT: Wrote out a cart list with " + cartList.tagCount() + " carts");
//  }

  @Override
  protected void writeCustomNBT(NBTTagCompound root) {
    // TODO Auto-generated method stub
    
  }

  @Override
  protected void readCustomNBT(NBTTagCompound root) {
    // TODO Auto-generated method stub
    
  }

}
