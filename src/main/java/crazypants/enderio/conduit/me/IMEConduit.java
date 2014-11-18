package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import appeng.api.parts.IPart;
import crazypants.enderio.conduit.IConduit;

public interface IMEConduit extends IConduit {

  MEConduitGrid getGrid();
  
  EnumSet<ForgeDirection> getConnections();
  
  boolean isDense();

  /**
   * Puts the part in the conduit facing the given direction.
   * 
   * @param player Player who is adding the part. This can be null if {@code stack} is null
   * @param stack Stack containing an {@link IPartItem}
   * @param dir Direction to add the part in
   */
  void setPart(EntityPlayer player, ItemStack stack, ForgeDirection dir);
  
  ItemStack getPartStack(ForgeDirection dir);
  
  IPart getPart(ForgeDirection dir);

}
