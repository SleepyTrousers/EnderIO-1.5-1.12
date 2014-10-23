package crazypants.enderio.rail;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class TileEnderRail extends TileEntity {

  private List<EntityMinecart> recievedCarts = new ArrayList<EntityMinecart>();
  
  //private List<EntityMinecart> cartsOnTrack = new ArrayList<EntityMinecart>();
  
  
  @Override
  public void updateEntity() {    
    List<EntityMinecart> carts = getMinecartsAt(getWorldObj(), xCoord, yCoord, zCoord);
    List<EntityMinecart> toRemove = new ArrayList<EntityMinecart>();
    for(EntityMinecart recievedCart : recievedCarts) {
      if(!carts.contains(recievedCart)) {
        //recievedCarts.remove(recievedCart);
        toRemove.add(recievedCart);
      }
    }
    for(EntityMinecart recievedCart : toRemove) {
      recievedCarts.remove(recievedCart);
    }
  }
  
  public void addRecievedCart(EntityMinecart mc) {
    recievedCarts.add(mc);
  }

  public boolean isRecievedCart(EntityMinecart mc) {
    return recievedCarts.contains(mc);
  }
  
  public boolean isClear() {
    List res = worldObj.getEntitiesWithinAABB(Entity.class, AxisAlignedBB.getBoundingBox(xCoord, yCoord + 1, zCoord, xCoord + 1, yCoord + 2, zCoord + 1));
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
        if (!cart.isDead) {
            carts.add((EntityMinecart) o);
        }
    }
    return carts;
}

  
  
}
