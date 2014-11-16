package crazypants.enderio.conduit.me;

import java.util.EnumSet;

import appeng.api.parts.IPart;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import crazypants.enderio.conduit.IConduit;

public interface IMEConduit extends IConduit {

  MEConduitGrid getGrid();
  
  EnumSet<ForgeDirection> getConnections();
  
  boolean isDense();

  void setPart(ItemStack stack, ForgeDirection dir);
  
  ItemStack getPartStack(ForgeDirection dir);
  
  IPart getPart(ForgeDirection dir);

}
