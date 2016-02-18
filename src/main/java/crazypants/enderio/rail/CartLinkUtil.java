package crazypants.enderio.rail;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class CartLinkUtil implements ICartLinkUtil {

  public static final ICartLinkUtil instance;

  public static final ICartLinkUtil defaultInstance = new CartLinkUtil();

  static {
//    ICartLinkUtil daInstance;
//    if(Loader.isModLoaded("Railcraft")) {
//      try {
//        daInstance = new RailcraftLinkUtil();
//      } catch (Exception e) {
//        daInstance = defaultInstance;
//      }
//    } else {
//      daInstance = defaultInstance;
//    }
//    instance = daInstance;
    instance = defaultInstance;
  }

  private CartLinkUtil() {
  }

  @Override
  public void setCartDirection(EntityMinecart cart, EnumFacing dir) {
    double velocity = Math.max(Math.abs(cart.motionX), Math.abs(cart.motionZ));
    cart.motionX = dir.getFrontOffsetX() * velocity;
    cart.motionZ = dir.getFrontOffsetZ() * velocity;
  }

  @Override
  public int getNumberOfCartsInTrain(EntityMinecart cart) {
    if(cart == null) {
      return 0;
    }
    return 1;
  }

  @Override
  public List<EntityMinecart> getCartsInTrain(EntityMinecart cart) {
    if(cart == null) {
      return Collections.emptyList();
    }
    return Collections.singletonList(cart);
  }

  @Override
  public void updateCartLinks(World world, EntityMinecart cart) {
  }

  @Override
  public void recreateLinks(EntityMinecart cart) {
  }

  @Override
  public void breakLinks(World worldObj, Entity entity) {
  }

}
