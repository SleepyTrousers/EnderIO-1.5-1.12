package crazypants.enderio.rail;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public interface ICartLinkUtil {

  int getNumberOfCartsInTrain(EntityMinecart cart);

  List<EntityMinecart> getCartsInTrain(EntityMinecart cart);

  void updateCartLinks(World world, EntityMinecart cart);

  void recreateLinks(EntityMinecart cart);

  void breakLinks(World worldObj, Entity entity);

  void setCartDirection(EntityMinecart cart, ForgeDirection dir);

}
