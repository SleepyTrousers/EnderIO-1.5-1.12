package crazypants.enderio.tool;

import crazypants.enderio.api.tool.ITool;
import net.minecraft.item.ItemStack;

public class BuildCraftToolProvider implements IToolProvider {

//  private BCWrench wrench = new BCWrench();

  public BuildCraftToolProvider() throws Exception {
    //Do a check for so we throw an exception in the constructor if we dont have the
    // wrench class
    Class.forName("buildcraft.api.tools.IToolWrench");
  }

  @Override
  public ITool getTool(ItemStack stack) {
    //TODO: 1.9
//    if(MpsUtil.instance.isPowerFistEquiped(stack) && !MpsUtil.instance.isOmniToolActive(stack)) {
//      return null;
//    }
//    if(stack.getItem() instanceof IToolWrench) {
//      return wrench;
//    }
    return null;
  }

}
