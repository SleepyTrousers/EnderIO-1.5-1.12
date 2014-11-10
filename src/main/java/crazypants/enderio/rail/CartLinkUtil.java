package crazypants.enderio.rail;

import java.util.Collections;
import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import cpw.mods.fml.common.Loader;

public class CartLinkUtil implements ICartLinkUtil {

  public static final ICartLinkUtil instance;

  static {
    ICartLinkUtil daInstance;
    if(Loader.isModLoaded("Railcraft")) {
      try {
        daInstance = new RailcraftLinkUtil();
      } catch (Exception e) {
        daInstance = new CartLinkUtil();
      }
    } else {
      daInstance = new CartLinkUtil();
    }
    instance = daInstance;
  }

  private CartLinkUtil() {
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
