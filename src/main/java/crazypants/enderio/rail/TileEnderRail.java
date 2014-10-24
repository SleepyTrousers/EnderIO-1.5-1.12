package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import crazypants.util.ForgeDirectionOffsets;
import crazypants.util.MetadataUtil;
import crazypants.vecmath.Vector3d;

import mods.railcraft.api.carts.CartTools;
import mods.railcraft.api.carts.ILinkageManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEnderRail extends TileEntity {

  //keep track of which carts we have received have left the track as they can't be teleported again until they have
  private final Set<EntityMinecart> newlySpawnedCarts = new HashSet<EntityMinecart>();  
  
  private final LinkedList<List<Entity>> entitiesToSpawn = new LinkedList<List<Entity>>();
  
  private int ticksFailedToSpawn = 0;
  
  @Override
  public void updateEntity() {
    
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
    if(entitiesToSpawn.isEmpty()) {
      ticksFailedToSpawn = 0;
      return;
    }
    if(!isClear()) {
      ticksFailedToSpawn++;
      if(ticksFailedToSpawn < 60) {
        return;
      }
    } 
    
    List<Entity> spawnThisTick = entitiesToSpawn.removeFirst();        
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

  public boolean isReverse() {
    return BlockEnderRail.isReverse(getBlockMetadata());
  }

  public ForgeDirection getDirection() {
    return BlockEnderRail.getDirection(getBlockMetadata());
  }

  public void onTrainRecieved(List<List<Entity>> toTeleport) {     
    entitiesToSpawn.addAll(toTeleport);
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

  @Override
  public void onChunkUnload() {
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
//    super.readFromNBT(root);
//  }
//
//  @Override
//  public void writeToNBT(NBTTagCompound root) {
//    super.writeToNBT(root);
//  }

}
